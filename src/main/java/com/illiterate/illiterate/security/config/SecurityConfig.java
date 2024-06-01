package com.illiterate.illiterate.security.config;


import com.illiterate.illiterate.security.JWT.JWTFilter;
import com.illiterate.illiterate.security.JWT.JWTProvider;
import com.illiterate.illiterate.security.exception.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static com.illiterate.illiterate.member.enums.RolesType.ROLE_ADMIN;
import static com.illiterate.illiterate.member.enums.RolesType.ROLE_USER;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTProvider jwtProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * 패스워드 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        //return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new BCryptPasswordEncoder();
    }

    /**
     * 로그인 인증 할때 사용함
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * public http
     */
    @Bean
    @Order(1)
    public SecurityFilterChain permitAllFilterChain(HttpSecurity http) throws Exception {
        httpSecuritySetting(http);
        http
                .securityMatchers(matcher -> matcher
                        .requestMatchers(permitAllRequestMatchers()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(permitAllRequestMatchers()).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    /**
     * 토큰 인증 및 권한이 필요한 http
     */
    @Bean
    @Order(2)
    public SecurityFilterChain authenticatedFilterChain(HttpSecurity http) throws Exception {
        httpSecuritySetting(http);
        http
                .securityMatchers(matcher -> matcher
                        .requestMatchers(AuthRequestMatchers()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AuthRequestMatchers())
                        .hasAnyAuthority(ROLE_USER.name(), ROLE_ADMIN.name())
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(new JWTFilter(jwtProvider), ExceptionTranslationFilter.class);
        return http.build();
    }

    @Bean    // no @Order defaults to last
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        httpSecuritySetting(http);
        http
                .securityMatchers(matcher -> matcher
                        .requestMatchers(OPTIONS, "/**")
                );

        return http.build();
    }

    /**
     * permitAll endpoint
     */
    private RequestMatcher[] permitAllRequestMatchers() {
        List<RequestMatcher> requestMatchers = List.of(
                antMatcher(POST, "/member/login"),            // 로그인
                antMatcher(POST, "/member/register"),        // 회원가입
                antMatcher(POST, "/certificate/phone"), // 메일 인증번호 전송
                antMatcher(GET, "/certificate/phone"), // 인증 번호 검증
                antMatcher(GET, "/certificate/phone/{phone}/status"), // 메일 인증 여부 확인
                antMatcher(POST, "/member/business/validate-info"), // 사업자등록정보 진위여부 확인
                antMatcher(POST, "/member/business/validate-status") // 사업자등록번호 상태조회
                //antMatcher(GET, "/member/health") // aws 상태 체크
        );

        return requestMatchers.toArray(RequestMatcher[]::new);
    }

    /**
     * JWT Authentication, Roles Authorization endpoint
     */
    private RequestMatcher[] AuthRequestMatchers() {
        List<RequestMatcher> requestMatchers = List.of(
                antMatcher(GET, "/user/{userId}"),                  // 회원 정보 조회
                antMatcher(PATCH, "/user/{userId}"),                // 회원 정보 수정
                antMatcher(PATCH, "/user/{userId}/password"),        // 비밀번호 재설정
                antMatcher(DELETE, "/user/{userId}")                // 회원 탈퇴
        );

        return requestMatchers.toArray(RequestMatcher[]::new);
    }

    /**
     * cors 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin(출처)d
        configuration.setAllowedOrigins(
                Arrays.asList(
                        "http://localhost:8080",
                        "http://127.0.0.1:8080",
                        "http://localhost:3000",
                        "http://127.0.0.1:3000",
                        "http://localhost:8000",
                        "http://127.0.0.1:8000",
                        "http://swagger-ui-integrated:8000"
                )
        );

        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE"
                )
        );

        // 허용할 헤더
        configuration.setAllowedHeaders(
                Arrays.asList(
                        "Authorization",
                        "Cache-Control",
                        "Content-Type"
                )
        );

        // 쿠키 및 인증 정보 전송
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void httpSecuritySetting(HttpSecurity http) throws Exception {
        http
                // jwt, OAuth 토큰을 사용 -> OAuth의 경우는 이슈가 발생할 수 있음 OAuth 구성할때 체크
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // cors 정책
                .formLogin(AbstractHttpConfigurer::disable) // form 기반 로그인을 사용하지 않음.
                .httpBasic(AbstractHttpConfigurer::disable) // 기본으로 제공하는 http 사용하지 않음
                .rememberMe(AbstractHttpConfigurer::disable) // 토큰 기반이므로 세션 기반의 인증 사용하지 않음
                .headers(headers -> headers.frameOptions(
                        HeadersConfigurer.FrameOptionsConfig::disable)) // x-Frame-Options 헤더 비활성화, 클릭재킹 공격 관련
                .logout(AbstractHttpConfigurer::disable) // stateful 하지 않기때문에 필요하지 않음
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 생성을 하지 않음
                )
                .anonymous(AbstractHttpConfigurer::disable); // 익명 사용자 접근 제한, 모든 요청이 인증 필요

    }
}