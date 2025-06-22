package Uniton.Fring.domain.member.service;

import Uniton.Fring.domain.like.repository.MemberLikeRepository;
import Uniton.Fring.domain.like.repository.ProductLikeRepository;
import Uniton.Fring.domain.like.repository.RecipeLikeRepository;
import Uniton.Fring.domain.member.dto.req.DeleteMemberRequestDto;
import Uniton.Fring.domain.member.dto.res.MemberInfoResponseDto;
import Uniton.Fring.domain.member.dto.res.SimpleMemberResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.enums.MemberRole;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.domain.recipe.entity.Recipe;
import Uniton.Fring.domain.recipe.repository.RecipeRepository;
import Uniton.Fring.domain.recipe.service.RecipeService;
import Uniton.Fring.domain.review.repository.ReviewRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.security.jwt.RefreshTokenRepository;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberLikeRepository memberLikeRepository;
    private final ProductLikeRepository productLikeRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeService recipeService;

    @Transactional(readOnly = true)
    public List<SimpleMemberResponseDto> searchMember(UserDetailsImpl userDetails, String keyword, int page) {

        log.info("[레시피 유저 검색 요청] keyword={}", keyword);

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        // Pageable은 springframework import
        Pageable pageable = PageRequest.of(page, 10);

        Page<Member> members = memberRepository.findByNicknameContaining(keyword, pageable);

        List<SimpleMemberResponseDto> simpleMemberResponseDtos = members.stream()
                .map(member -> {
                    Boolean isLikedMember = null;

                    if (memberId != null) {
                        isLikedMember = memberLikeRepository.existsByMemberIdAndLikedMemberId(memberId, member.getId());
                    }

                    return SimpleMemberResponseDto.builder().member(member).isLikedMember(isLikedMember).build();
                }).toList();

        log.info("[레시피 유저 검색 요청 성공] keyword={}", keyword);

        return simpleMemberResponseDtos;
    }

    @Transactional(readOnly = true)
    public List<SimpleMemberResponseDto> getRankingRecipeMember(UserDetailsImpl userDetails) {

        log.info("[유저 랭킹 조회 요청]");

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        List<Member> members =  memberRepository.findTop5ByOrderByLikeCountDesc();

        List<SimpleMemberResponseDto> simpleMemberResponseDtos = members.stream()
                .map(member -> {
                    Boolean isLikedMember = null;

                    if (memberId != null) {
                        isLikedMember = memberLikeRepository.existsByMemberIdAndLikedMemberId(memberId, member.getId());
                    }

                    return SimpleMemberResponseDto.builder().member(member).likeCount(member.getLikeCount()).isLikedMember(isLikedMember).build();
                }).toList();

        log.info("[레시피 유저 랭킹 조회 성공]");

        return simpleMemberResponseDtos;
    }

    @Transactional(readOnly = true)
    public MemberInfoResponseDto getMemberInfo(UserDetailsImpl userDetails, Long memberId, int page) {

        log.info("[유저 정보 조회 요청]");

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("[유저 정보 조회 실패] 사용자 없음");
                    return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                });

        Long mineMemberId;
        if (userDetails != null) { mineMemberId = userDetails.getMember().getId(); } else {
            mineMemberId = null;
        }

        // 평점 기준 정렬
        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "rating"));

        // 레시피 목록, 개수 조회
        Page<Recipe> recipes = recipeRepository.findByMemberId(memberId, pageable);

        Map<Long, Integer> reviewCountMap = recipeService.getReviewCountMapFromRecipes(recipes.getContent());

        List<SimpleRecipeResponseDto> simpleRecipeResponseDtos = recipes.stream()
                .map(recipe -> {
                    Boolean isLikedRecipe = null;
                    Integer reviewCount = reviewCountMap.getOrDefault(recipe.getId(), 0);

                    if (mineMemberId != null) {
                        isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    }

                    return SimpleRecipeResponseDto.builder().recipe(recipe).isLiked(isLikedRecipe).reviewCount(reviewCount).build();
                }).toList();
        int recipeCount = (int) recipes.getTotalElements();

        // 소비자 유저 정보 조회
        if (member.getRole() == MemberRole.CONSUMER) {
            log.info("[소비자 유저 정보 조회 성공]");
            return MemberInfoResponseDto.fromConsumer(member, simpleRecipeResponseDtos, recipeCount);
        }

        // 농부 유저 정보 조회
        Page<Product> products = productRepository.findByMemberId(memberId, pageable);
        List<SimpleProductResponseDto> simpleProductResponseDtos = products.stream()
                .map(product -> {
                    Boolean isLikedProduct = null;

                    if (mineMemberId != null) {
                        isLikedProduct = productLikeRepository.existsByMemberIdAndProductId(memberId, product.getId());
                    }

                    return SimpleProductResponseDto.builder().product(product).isLiked(isLikedProduct).build();
                }).toList();
        int productCount = (int) products.getTotalElements();

        log.info("[농부 유저 정보 조회 성공]");
        return MemberInfoResponseDto.fromFarmer(member, simpleRecipeResponseDtos, recipeCount, simpleProductResponseDtos, productCount);
    }

    @Transactional
    public void deleteMember(Member member, DeleteMemberRequestDto deleteMemberRequestDto) {

        log.info("[회원 탈퇴 요청 시작] email={}}", member.getEmail());

        Member memberToDelete = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> {
                    log.warn("[회원 정보 조회 실패] 사용자 없음: email={}", member.getEmail());
                    return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                });

        boolean matchPassword = passwordEncoder.matches(deleteMemberRequestDto.getPassword(), memberToDelete.getPassword());
        if (!matchPassword) {
            log.warn("[회원 탈퇴 실패] 비밀번호 불일치: email={}", member.getEmail());
            throw new CustomException(ErrorCode.PASSWORD_NOT_CORRECT);
        }
        log.info("[비밀번호 확인 성공] 회원 탈퇴 진행 (nickname: {})", memberToDelete.getNickname());

        refreshTokenRepository.deleteByMemberId(memberToDelete.getId());

        memberRepository.delete(memberToDelete);

        log.info("[회원 탈퇴 완료] nickname: {}, email={}", memberToDelete.getNickname(), memberToDelete.getEmail());
    }
}
