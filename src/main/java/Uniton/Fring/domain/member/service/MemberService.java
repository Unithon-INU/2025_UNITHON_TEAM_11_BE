package Uniton.Fring.domain.member.service;

import Uniton.Fring.domain.like.repository.MemberLikeRepository;
import Uniton.Fring.domain.like.repository.ProductLikeRepository;
import Uniton.Fring.domain.like.repository.RecipeLikeRepository;
import Uniton.Fring.domain.member.dto.req.DeleteMemberRequestDto;
import Uniton.Fring.domain.member.dto.req.LoginRequestDto;
import Uniton.Fring.domain.member.dto.req.SignupRequestDto;
import Uniton.Fring.domain.member.dto.res.LoginResponseDto;
import Uniton.Fring.domain.member.dto.res.MemberInfoResponseDto;
import Uniton.Fring.domain.member.dto.res.SignupResponseDto;
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
import Uniton.Fring.domain.review.repository.ReviewRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.s3.S3Service;
import Uniton.Fring.global.security.jwt.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberLikeRepository memberLikeRepository;
    private final ProductLikeRepository productLikeRepository;
    private final RecipeLikeRepository recipeLikeRepository;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto, MultipartFile image) {

        log.info("[회원가입 요청] email={}, username={}, nickname={}", signupRequestDto.getEmail(), signupRequestDto.getUsername(), signupRequestDto.getNickname());

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = s3Service.upload(image, "profileImages");
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FILE_CONVERT_FAIL);
            }
        }

        Member member = new Member(signupRequestDto, passwordEncoder.encode(signupRequestDto.getPassword()), imageUrl);

        memberRepository.save(member);

        log.info("[회원가입 완료] memberId={}, username={}, email={}", member.getId(),member.getUsername(), member.getEmail());

        return SignupResponseDto.builder()
                .username(member.getUsername())
                .nickname(member.getNickname())
                .build();
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        log.info("[로그인 요청] username={}", loginRequestDto.getUsername());

        Member member = memberRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> {
                    log.warn("[로그인 실패] 사용자 없음: username={}", loginRequestDto.getUsername());
                    return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                });

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            log.warn("[로그인 실패] 비밀번호 불일치: username={}", loginRequestDto.getUsername());
            throw new CustomException(ErrorCode.PASSWORD_NOT_CORRECT);
        }

        log.info("[로그인 성공] username={}", member.getUsername());

        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginRequestDto.toAuthenticationToken();
        log.debug("Spring Security 인증 시작");

        // Spring Security 인증 수행
        // 내부적으로 UserDetailsService 호출
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증 상태 확인
        if (!authentication.isAuthenticated()) {
            log.error("[로그인 실패] 인증 실패: username={}", loginRequestDto.getUsername());
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        // 리프레시 토큰 존재 시 삭제
        if (refreshTokenRepository.findByUsername(member.getUsername()).isPresent()) {
            refreshTokenRepository.deleteByUsername(member.getUsername());
        }

        // JWT 토큰 생성
        LoginResponseDto loginResponseDto = jwtTokenProvider.generateToken(authentication);
        log.info("[JWT 발급 완료] memberId={}, email={}", member.getId(), member.getEmail());

        RefreshToken refreshToken = RefreshToken.builder().member(member).refreshToken(loginResponseDto.getRefreshToken()).build();
        refreshTokenRepository.save(refreshToken);

        return loginResponseDto;
    }

    @Transactional
    public LoginResponseDto refresh(JwtTokenRequestDto jwtTokenRequestDto) {

        log.info("[JWT 토큰 재발급 요청]");

        if (!jwtTokenProvider.validateToken(jwtTokenRequestDto.getRefreshToken())) {
            log.warn("[JWT 토큰 유효성 검증 실패] 만료된 Refresh Token");
            throw new CustomException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(jwtTokenRequestDto.getRefreshToken());
        log.info("[인증 정보 추출 완료] username={}", authentication.getName());

        String username = authentication.getName();
        log.info("[RefreshToken 조회 시도] username={}", username);
        RefreshToken refreshToken = refreshTokenRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> {
                    log.warn("[RefreshToken 조회 실패] 저장된 토큰 없음 (username={})", username);
                    return new CustomException(ErrorCode.JWT_NOT_FOUND);
                });

        if (!refreshToken.getRefreshToken().equals(jwtTokenRequestDto.getRefreshToken())) {
            log.warn("[RefreshToken 불일치] 요청 토큰과 저장 토큰이 다름 (username={})", username);
            throw new CustomException(ErrorCode.JWT_NOT_MATCH);
        }

        log.info("[RefreshToken 일치 확인 완료] 새 AccessToken 및 RefreshToken 생성 시작");

        LoginResponseDto loginResponseDto = jwtTokenProvider.generateToken(authentication);
        log.info("[AccessToken/RefreshToken 재발급 완료] username={}", username);

        RefreshToken newRefreshToken = refreshToken.updateValue(loginResponseDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);
        log.info("[새 RefreshToken 저장 완료] email={}", username);

        return loginResponseDto;
    }

    @Transactional(readOnly = true)
    public Boolean checkEmailDuplicated(String email) {

        log.info("[이메일 중복 확인] email={}", email);

        if (memberRepository.existsByEmail(email)) {
            log.warn("[중복 이메일] email={}", email);
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
        return true;
    }

    @Transactional(readOnly = true)
    public Boolean checkUsernameDuplicated(String username) {

        log.info("[아이디 중복 확인] username={}", username);

        if (memberRepository.existsByUsername(username)) {
            log.warn("[중복 아이디] username={}", username);
            throw new CustomException(ErrorCode.USERNAME_DUPLICATED);
        }
        return true;
    }

    @Transactional(readOnly = true)
    public Boolean checkNicknameDuplicated(String nickname) {

        log.info("[닉네임 중복 확인] nickname={}", nickname);

        if (memberRepository.existsByNickname(nickname)) {
            log.warn("[중복 닉네임] nickname={}", nickname);
            throw new CustomException(ErrorCode.NICKNAME_DUPLICATED);
        }
        return true;
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

    @Transactional
    public void changeToFarmer(Member member) {

        log.info("[회원 ROLE 농부로 변경 요청] email={}", member.getEmail());

        Member newMember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> {
                    log.warn("[회원 정보 조회 실패] 사용자 없음: email={}", member.getEmail());
                    return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                });

        newMember.changeRoleToFarmer();

        log.info("[회원 ROLE 농부로 변경 완료] email={}", member.getEmail());
    }

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
        List<SimpleRecipeResponseDto> simpleRecipeResponseDtos = recipes.stream()
                .map(recipe -> {
                    Boolean isLikedRecipe = null;
                    Integer reviewCount = reviewRepository.countByRecipeId(recipe.getId());

                    if (mineMemberId != null) {
                        isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    }

                    return SimpleRecipeResponseDto.builder().recipe(recipe).isLiked(isLikedRecipe).commentCount(reviewCount).build();
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
}
