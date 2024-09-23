package com.illiterate.illiterate.security.Config;

import com.illiterate.illiterate.security.Exception.CustomAccessDeniedHandler;
import com.illiterate.illiterate.security.Service.MemberUserDetailsService;
import com.illiterate.illiterate.security.jwt.JwtFilter;
import com.illiterate.illiterate.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static com.illiterate.illiterate.member.enums.RolesType.ROLE_ADMIN;
import static com.illiterate.illiterate.member.enums.RolesType.ROLE_USER;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final MemberUserDetailsService memberUserDetailsService;

    public SecurityConfig(JwtProvider jwtProvider,
                                CustomAccessDeniedHandler customAccessDeniedHandler,
                                MemberUserDetailsService bringUserDetailsService) {
        this.jwtProvider = jwtProvider;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.memberUserDetailsService = bringUserDetailsService;
    }

    /**
     * RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


    /**
     * 패스워드 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 로그인 인증 할때 사용함
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(memberUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(authenticationProvider()));
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
                .addFilterBefore(new JwtFilter(jwtProvider), ExceptionTranslationFilter.class);
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
                antMatcher("/public/**")
        );

        return requestMatchers.toArray(RequestMatcher[]::new);
    }

    /**
     * JWT Authentication, Roles Authorization endpoint
     */
    private RequestMatcher[] AuthRequestMatchers() {
        List<RequestMatcher> requestMatchers = List.of(
                antMatcher("/ocr/**"),
                antMatcher("/board/**"),
                antMatcher("/user/**"),
                antMatcher("/userinfo"),
                antMatcher("/admin/**")
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
                        "http://127.0.0.1:8000"
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