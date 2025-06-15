package Uniton.Fring.domain.member.service;

import Uniton.Fring.domain.member.dto.req.DeleteMemberRequestDto;
import Uniton.Fring.domain.member.dto.req.LoginRequestDto;
import Uniton.Fring.domain.member.dto.req.SignupRequestDto;
import Uniton.Fring.domain.member.dto.res.LoginResponseDto;
import Uniton.Fring.domain.member.dto.res.MemberRankingResponseDto;
import Uniton.Fring.domain.member.dto.res.SearchMemberResponseDto;
import Uniton.Fring.domain.member.dto.res.SignupResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.security.jwt.JwtTokenProvider;
import Uniton.Fring.global.security.jwt.JwtTokenRequestDto;
import Uniton.Fring.global.security.jwt.RefreshToken;
import Uniton.Fring.global.security.jwt.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {

        log.info("[회원가입 요청] email={}, username={}, nickname={}", signupRequestDto.getEmail(), signupRequestDto.getUsername(), signupRequestDto.getNickname());

        Member member = new Member(signupRequestDto, passwordEncoder.encode(signupRequestDto.getPassword()));

        memberRepository.save(member);

        log.info("[회원가입 완료] memberId={}, username={}, email={}", member.getId(),member.getUsername(), member.getEmail());

        return SignupResponseDto.builder()
                .username(member.getUsername())
                .nickname(member.getNickname())
                .build();
    }

    @Transactional(readOnly = true)
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

        // JWT 토큰 생성
        LoginResponseDto loginResponseDto = jwtTokenProvider.generateToken(authentication);
        log.info("[JWT 발급 완료] memberId={}, email={}", member.getId(), member.getEmail());

        return loginResponseDto;
    }

//    public Boolean updatePassword(@Valid UpdatePasswordRequestDto updatePasswordRequestDto) {
//
//
//    }

    @Transactional
    public LoginResponseDto refresh(JwtTokenRequestDto jwtTokenRequestDto) {

        log.info("[JWT 토큰 재발급 요청]");

        if (!jwtTokenProvider.validateToken(jwtTokenRequestDto.getRefreshToken())) {
            log.warn("[JWT 토큰 유효성 검증 실패] 만료된 Refresh Token");
            throw new CustomException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(jwtTokenRequestDto.getRefreshToken());
        log.info("[인증 정보 추출 완료] email={}", authentication.getName());

        String email = authentication.getName();
        log.info("[RefreshToken 조회 시도] email={}", email);
        RefreshToken refreshToken = refreshTokenRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> {
                    log.warn("[RefreshToken 조회 실패] 저장된 토큰 없음 (email={})", email);
                    return new CustomException(ErrorCode.JWT_NOT_FOUND);
                });

        if (!refreshToken.getRefreshToken().equals(jwtTokenRequestDto.getRefreshToken())) {
            log.warn("[RefreshToken 불일치] 요청 토큰과 저장 토큰이 다름 (email={})", email);
            throw new CustomException(ErrorCode.JWT_NOT_MATCH);
        }

        log.info("[RefreshToken 일치 확인 완료] 새 AccessToken 및 RefreshToken 생성 시작");

        LoginResponseDto loginResponseDto = jwtTokenProvider.generateToken(authentication);
        log.info("[AccessToken/RefreshToken 재발급 완료] email={}", email);

        RefreshToken newRefreshToken = refreshToken.updateValue(loginResponseDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);
        log.info("[새 RefreshToken 저장 완료] email={}", email);

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
    public List<SearchMemberResponseDto> searchMember(String keyword, Pageable pageable) {

        log.info("[레시피 유저 검색 요청] keyword={}", keyword);

        Page<Member> members = memberRepository.findByNicknameContaining(keyword, pageable);

        List<SearchMemberResponseDto> searchMemberResponseDtos = members.stream()
                .map(member -> SearchMemberResponseDto.builder().member(member).build()).toList();

        log.info("[레시피 유저 검색 요청 성공] keyword={}", keyword);

        return searchMemberResponseDtos;
    }

    @Transactional(readOnly = true)
    public List<MemberRankingResponseDto> getRankingRecipeMember() {

        log.info("[유저 랭킹 조회 요청]");

        List<Member> members =  memberRepository.find5ByOrderByLikeCountDesc();

        List<MemberRankingResponseDto> memberRankingResponseDtos = members.stream()
                .map(member -> MemberRankingResponseDto.builder().member(member).build()).toList();

        log.info("[레시피 유저 랭킹 조회 성공]");

        return memberRankingResponseDtos;
    }
}
