package Uniton.Fring.global.security.jwt;

import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.exception.ErrorResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // HTTP 헤더에서 JWT 토큰이 담기는 키 이름
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더에서 JWT 토큰 추출
        String jwt = resolveToken(request);
        // 요청 URI 정보 저장
        String requestURI = request.getRequestURI();

        // 리프레시 토큰 발급 API는 Access Token 검증 건너뜀
        if ("/api/auth/refresh".equals(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // JWT 토큰이 존재하고 유효하면 인증 객체를 SecurityContext에 저장
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt, "access")) {

                // 토큰 기반 인증 정보 생성
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);

                // SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("Security Context : " + authentication.getName() + "\nURI : " + requestURI);
            }

            // 다음 필터로 요청 / 응답 객체 전달
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            // 커스텀 예외 응답
            setErrorResponse(response, e.getErrorCode());
        } catch (Exception e) {
            // 기타 예외 응답
            setErrorResponse(response, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 요청 헤더에서 "Authorization" 키로부터 "Bearer " 접두어를 제거하고 JWT 토큰만 추출하는 메서드
    private String resolveToken(HttpServletRequest request) {

        // Authorization 헤더 값 조회
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        // 헤더가 존재하고 "Bearer "로 시작하면 토큰 부분만 잘라서 반환
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getCode());
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponseEntity errorResponse = ErrorResponseEntity.toResponseEntity(errorCode).getBody();
        String json = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(json);
    }
}
