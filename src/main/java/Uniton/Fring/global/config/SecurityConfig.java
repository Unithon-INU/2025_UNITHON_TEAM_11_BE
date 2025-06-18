package Uniton.Fring.global.config;

import Uniton.Fring.global.security.jwt.JwtFilter;
import Uniton.Fring.global.security.jwt.JwtTokenProvider;
import Uniton.Fring.global.security.jwt.handler.JwtAccessDeniedHandler;
import Uniton.Fring.global.security.jwt.handler.JwtAuthenticationEntryPoint;
import Uniton.Fring.global.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import Uniton.Fring.global.security.oauth2.handler.OAuth2AuthenticationFailureHandler;
import Uniton.Fring.global.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import Uniton.Fring.global.security.oauth2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    // 비밀번호 암호화 빈
    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 필터 체인 구성
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource, HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository, CustomOAuth2UserService customOAuth2UserService) throws Exception {
        http
                // 설정된 CORS 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // 기본 HTTP 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // CSRF 보호 비활성화 ( STATELESS 특성 )
                .csrf(AbstractHttpConfigurer::disable)

                // 요청 권한 설정 ( 해당 엔드포인트에 대해서는 허용 )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SIGNUP_URIS).permitAll()
                        .requestMatchers(SWAGGER_URIS).permitAll()
                        .requestMatchers(VIEW_URIS).permitAll()
                        .anyRequest().authenticated()
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
//                        .access( new WebExpressionAuthorizationManager(
//                                "hasIpAddress('219.248.253.212') or hasIpAddress('210.123.73.85')"
//                        ))
                )

                // 예외 처리 설정
                .exceptionHandling(exceptioin -> exceptioin
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                // 세션 정책 설정 ( STATELESS )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // JWT 필터를 앞에 설정
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                // OAUTH 2.0
                .oauth2Login(configure -> configure
                        .authorizationEndpoint(config -> config.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository))
                        .userInfoEndpoint(config -> config.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                );

        return http.build();
    }

    private static final String[] SIGNUP_URIS = {
            "/api/members/signup",
            "/api/members/login",
            "/api/members/username/**",
            "/api/members/email/**",
            "/api/members/nickname/**",
            "/api/members/login/oauth2/**",
            "/api/mails/**"
    };

    private static final String[] SWAGGER_URIS = {
            "/favicon.ico",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    private static final String[] VIEW_URIS = {
            "/api/main/**",
    };
}
