package Uniton.Fring.domain.recipe.service;

import Uniton.Fring.domain.like.repository.MemberLikeRepository;
import Uniton.Fring.domain.like.repository.RecipeLikeRepository;
import Uniton.Fring.domain.like.repository.ReviewLikeRepository;
import Uniton.Fring.domain.member.dto.res.MemberInfoResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.recipe.dto.req.RecipeRequestDto;
import Uniton.Fring.domain.recipe.dto.req.RecipeStepRequestDto;
import Uniton.Fring.domain.recipe.dto.res.RecipeInfoResponseDto;
import Uniton.Fring.domain.recipe.dto.res.RecipeStepResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.domain.recipe.entity.Recipe;
import Uniton.Fring.domain.recipe.entity.RecipeStep;
import Uniton.Fring.domain.recipe.repository.RecipeRepository;
import Uniton.Fring.domain.recipe.repository.RecipeStepRepository;
import Uniton.Fring.domain.review.dto.res.CommentResponseDto;
import Uniton.Fring.domain.review.dto.res.ReviewResponseDto;
import Uniton.Fring.domain.review.entity.Comment;
import Uniton.Fring.domain.review.entity.Review;
import Uniton.Fring.domain.review.repository.CommentRepository;
import Uniton.Fring.domain.review.repository.ReviewRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.s3.S3Service;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final S3Service s3Service;
    private final RecipeRepository recipeRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final MemberLikeRepository memberLikeRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Transactional(readOnly = true)
    public RecipeInfoResponseDto getRecipe(UserDetailsImpl userDetails, Long recipeId, int page) {

        log.info("[레시피 상세 정보 조회 요청]");

        // 리뷰 페이지
        Pageable reviewPageable = PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "likeCount"));
        // 댓글 페이지
        Pageable commmentPageable = PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    log.warn("[레시피 조회 실패] 레시피 없음: recipeId={}", recipeId);
                    return new CustomException(ErrorCode.RECIPE_NOT_FOUND);
                });

        Member member = memberRepository.findById(recipe.getMemberId())
                .orElseThrow(() -> {
                    log.warn("[회원 정보 조회 실패] 회원 없음: memberId={}", recipe.getMemberId());
                    return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                });

        Boolean isLikedRecipe = null;
        if (memberId != null) {
            isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
        }

        List<RecipeStep> steps = recipeStepRepository.findByRecipeId(recipeId);

        List<RecipeStepResponseDto> recipeStepResponseDtos = steps.stream()
                        .map(step -> RecipeStepResponseDto.builder().recipeStep(step).build()).toList();

        log.info("[레시피 순서 응답 생성]");

        Boolean isLikedMember = null;
        if (memberId != null) {
            isLikedMember = memberLikeRepository.existsByMemberIdAndLikedMemberId(memberId, member.getId());
        }

        // 작성자 정보
        MemberInfoResponseDto memberInfoResponseDto = MemberInfoResponseDto.fromMember(member, isLikedMember);

        log.info("[작성 회원 정보 응답 생성]");

        // 리뷰 페이지 조회
        Page<Review> reviewPage = reviewRepository.findByRecipeId(recipeId, reviewPageable);

        // 리뷰에서 memberId만 추출하고 중복 제거하여 리스트 생성
        List<Long> reviewMemberIds = reviewPage.getContent().stream()
                .map(Review::getMemberId)
                .distinct()
                .collect(Collectors.toList());
        // 리뷰에 등장하는 멤버들을 한 번에 조회
        List<Member> reviewMembers = memberRepository.findAllById(reviewMemberIds);
        // 멤버 리스트를 Map 형태로 변환 (멤버ID → 멤버 객체)
        Map<Long, Member> reviewMemberMap = reviewMembers.stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        List<ReviewResponseDto> reviewResponseDtoList = reviewPage.getContent().stream()
                .map(review -> {
                    // 해당 리뷰 작성자 정보 가져오기 (memberMap에서 꺼냄)
                    Member reviewer = reviewMemberMap.get(review.getMemberId());
                    if (reviewer == null) {
                        throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                    }

                    MemberInfoResponseDto reviewerInfoResponseDto = MemberInfoResponseDto.fromReviewer(reviewer);

                    Boolean isLikedReview = null;
                    if (memberId != null) {
                        isLikedReview = reviewLikeRepository.existsByMemberIdAndReviewId(memberId, review.getId());
                    }

                    return ReviewResponseDto.builder()
                            .review(review)
                            .memberInfo(reviewerInfoResponseDto)
                            .isLiked(isLikedReview)
                            .build();
                })
                .toList();

        log.info("[레시피 리뷰 리스트 응답 생성]");

        // 댓글 페이지 조회
        Page<Comment> commentPage = commentRepository.findByRecipeId(recipeId, commmentPageable);

        // 댓글에서 memberId만 추출하고 중복 제거하여 리스트 생성
        List<Long> commentMemberIds = commentPage.getContent().stream()
                .map(Comment::getMemberId)
                .distinct()
                .collect(Collectors.toList());
        // 댓글에 등장하는 멤버들을 한 번에 조회
        List<Member> commentMembers = memberRepository.findAllById(commentMemberIds);
        // 멤버 리스트를 Map 형태로 변환 (멤버ID → 멤버 객체)
        Map<Long, Member> commentMemberMap = commentMembers.stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        List<CommentResponseDto> commentResponseDtoList = commentPage.getContent().stream()
                .map(comment -> {
                    // 해당 리뷰 작성자 정보 가져오기 (memberMap에서 꺼냄)
                    Member commenter = commentMemberMap.get(comment.getMemberId());
                    if (commenter == null) {
                        throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                    }

                    MemberInfoResponseDto commenterInfoResponseDto = MemberInfoResponseDto.fromReviewer(commenter);

                    return CommentResponseDto.builder()
                            .comment(comment)
                            .memberInfo(commenterInfoResponseDto)
                            .build();
                })
                .toList();

        log.info("[레시피 댓글 리스트 응답 생성]");

        log.info("[레시피 상세 정보 조회 성공]");

        return RecipeInfoResponseDto.builder()
                .member(memberInfoResponseDto)
                .isLiked(isLikedRecipe)
                .recipe(recipe).recipeStep(recipeStepResponseDtos)
                .reviews(reviewResponseDtoList)
                .comments(commentResponseDtoList)
                .build();
    }

    @Transactional(readOnly = true)
    public List<SimpleRecipeResponseDto> getBestRecipeList(UserDetailsImpl userDetails) {

        log.info("[요즘 핫한 레시피 더보기 요청]");

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        List<Recipe> bestRecipes = recipeRepository.findTop10ByOrderByRatingDesc();

        Map<Long, Integer> reviewCountMap = getReviewCountMapFromRecipes(bestRecipes);

        List<SimpleRecipeResponseDto> bestRecipeResponseDtos = bestRecipes.stream()
                .map(recipe -> {
                    Boolean isLikedRecipe = null;
                    Integer reviewCount = reviewCountMap.getOrDefault(recipe.getId(), 0);

                    if (memberId != null) {
                        isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    }

                    return SimpleRecipeResponseDto.builder().recipe(recipe).isLiked(isLikedRecipe).reviewCount(reviewCount).build();
                }).toList();

        log.info("[요즘 핫한 레시피 더보기 성공]");

        return bestRecipeResponseDtos;
    }

    @Transactional(readOnly = true)
    public List<SimpleRecipeResponseDto> getRecipeList(UserDetailsImpl userDetails, int page) {

        log.info("[전체 레시피 목록 요청]");

        Pageable pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "createdAt"));

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        Page<Recipe> recentRecipes = recipeRepository.findAll(pageable);
        log.info("레시피 8개 조회 완료");

        Map<Long, Integer> reviewCountMap = getReviewCountMapFromRecipes(recentRecipes.getContent());

        List<SimpleRecipeResponseDto> recentRecipeResponseDtos = recentRecipes.stream()
                .map(recipe -> {
                    Boolean isLikedRecipe = null;
                    Integer reviewCount = reviewCountMap.getOrDefault(recipe.getId(), 0);

                    if (memberId != null) {
                        isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    }

                    return SimpleRecipeResponseDto.builder().recipe(recipe).isLiked(isLikedRecipe).reviewCount(reviewCount).build();
                }).toList();

        log.info("[전체 레시피 목록 조회 성공]");

        return recentRecipeResponseDtos;
    }

    @Transactional
    public RecipeInfoResponseDto addRecipe(UserDetailsImpl userDetails, RecipeRequestDto recipeRequestDto,
                                           MultipartFile mainImage, List<MultipartFile> descriptionImages) {

        log.info("[레시피 등록 요청]");

        Member member = userDetails.getMember();
        MemberInfoResponseDto memberInfoResponseDto = MemberInfoResponseDto.fromMember(member, null);

        Pair<String, List<String>> imageData = s3Service.uploadMainAndDescriptionImages(
                mainImage, descriptionImages, "recipes", "recipeSteps");

        String mainImageUrl = imageData.getFirst();
        List<String> recipeDescriptionImages = imageData.getSecond();

        Recipe recipe = Recipe.builder()
                .memberId(member.getId())
                .recipeRequestDto(recipeRequestDto)
                .mainImageUrl(mainImageUrl)
                .build();
        recipeRepository.save(recipe);

        log.info("[레시피 추가]");

        List<RecipeStepResponseDto> recipeStepResponseDtos = new ArrayList<>();
        List<RecipeStep> steps = new ArrayList<>();
        for (int i = 0; i < recipeRequestDto.getSteps().size(); i++) {
            RecipeStepRequestDto stepDto = recipeRequestDto.getSteps().get(i);

            RecipeStep step = RecipeStep.builder()
                    .recipeId(recipe.getId())
                    .imageUrl(recipeDescriptionImages.get(i))
                    .recipeStepRequestDto(stepDto)
                    .build();

            steps.add(step);
            recipeStepResponseDtos.add(RecipeStepResponseDto.builder().recipeStep(step).build());
        }
        recipeStepRepository.saveAll(steps);

        log.info("[레시피 순서 추가]");

        log.info("[레시피 등록 성공]");

        return RecipeInfoResponseDto.builder()
                .member(memberInfoResponseDto)
                .recipe(recipe).recipeStep(recipeStepResponseDtos)
                .isLiked(null)
                .reviews(new ArrayList<>())
                .comments(new ArrayList<>())
                .totalReviewCount(0)
                .recentImageUrls(new ArrayList<>())
                .build();
    }

    @Transactional
    public RecipeInfoResponseDto updateRecipe(UserDetailsImpl userDetails, Long recipeId,
                                              RecipeRequestDto recipeRequestDto,
                                              MultipartFile mainImage, List<MultipartFile> descriptionImages) {

        log.info("[레시피 수정 요청]");

        Member member = userDetails.getMember();
        MemberInfoResponseDto memberInfoResponseDto = MemberInfoResponseDto.fromMember(userDetails.getMember(), null);

        Recipe recipe = recipeRepository.findById(recipeId)
                        .orElseThrow(() -> {
                            log.warn("[레시피 수정 실패] 레시피 없음: recipeId={}", recipeId);
                            return new CustomException(ErrorCode.RECIPE_NOT_FOUND);
                        });

        if (!recipe.getMemberId().equals(member.getId())) {
            log.warn("[레시피 수정 실패] 사용자 권한 없음: memberId={}, recipeOwnerId={}", member.getId(), recipe.getMemberId());
            throw new CustomException(ErrorCode.RECIPE_MEMBER_NOT_MATCH);
        }

        // 기존 이미지들 백업
        String oldMainImage = recipe.getImageUrl();
        List<String> oldStepImageUrls = recipeStepRepository.findImageUrlsByRecipeId(recipeId);

        Pair<String, List<String>> imageData = s3Service.uploadMainAndDescriptionImages(
                mainImage, descriptionImages, "recipes", "recipeSteps");

        String mainImageUrl = imageData.getFirst();
        List<String> recipeDescriptionImages = imageData.getSecond();

        recipe.updateRecipe(recipeRequestDto, mainImageUrl);
        log.info("[레시피 엔티티 업데이트 성공] recipeId={}", recipeId);

        recipeStepRepository.deleteByRecipeId(recipeId);

        List<RecipeStepResponseDto> recipeStepResponseDtos = new ArrayList<>();
        List<RecipeStep> steps = new ArrayList<>();
        for (int i = 0; i < recipeRequestDto.getSteps().size(); i++) {
            RecipeStepRequestDto stepDto = recipeRequestDto.getSteps().get(i);

            RecipeStep step = RecipeStep.builder()
                    .recipeId(recipe.getId())
                    .imageUrl(recipeDescriptionImages.get(i))
                    .recipeStepRequestDto(stepDto)
                    .build();

            steps.add(step);
            recipeStepResponseDtos.add(RecipeStepResponseDto.builder().recipeStep(step).build());
        }
        recipeStepRepository.saveAll(steps);

        log.info("[레시피 수정 성공] recipeId={}", recipeId);

        if (oldMainImage != null && !oldMainImage.isBlank()) {
            s3Service.delete(oldMainImage);
        }
        for (String url : oldStepImageUrls) {
            if (url != null && !url.isBlank()) {
                s3Service.delete(url);
            }
        }

        return RecipeInfoResponseDto.builder()
                .member(memberInfoResponseDto)
                .recipe(recipe).recipeStep(recipeStepResponseDtos)
                .isLiked(null)
                .reviews(null)
                .comments(null)
                .totalReviewCount(null)
                .recentImageUrls(null)
                .build();
    }

    @Transactional
    public void deleteRecipe(UserDetailsImpl userDetails, Long recipeId) {

        log.info("[레시피 삭제 요청] recipeId={}", recipeId);

        Member member = userDetails.getMember();

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    log.warn("[레시피 삭제 실패]");
                    return new CustomException(ErrorCode.RECIPE_NOT_FOUND);
                });

        if (!recipe.getMemberId().equals(member.getId())) {
            log.warn("[레시피 삭제 실패] 사용자 권한 없음: memberId={}, recipeOwnerId={}", member.getId(), recipe.getMemberId());
            throw new CustomException(ErrorCode.RECIPE_MEMBER_NOT_MATCH);
        }

        // 기존 이미지 백업
        String mainImageUrl = recipe.getImageUrl();
        List<String> stepImageUrls = recipeStepRepository.findImageUrlsByRecipeId(recipeId);

        recipeStepRepository.deleteByRecipeId(recipeId);
        recipeRepository.deleteById(recipeId);

        log.info("[레시피 삭제 성공]");

        if (mainImageUrl != null && !mainImageUrl.isBlank()) {
            s3Service.delete(mainImageUrl);
        }
        for (String url : stepImageUrls) {
            if (url != null && !url.isBlank()) {
                s3Service.delete(url);
            }
        }
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> getReviewCountMapFromRecipes(List<Recipe> recipes) {
        List<Long> recipeIds = recipes.stream().map(Recipe::getId).toList();

        List<Object[]> reviewCounts = reviewRepository.countReviewsByRecipeIds(recipeIds);

        return reviewCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
    }
}
