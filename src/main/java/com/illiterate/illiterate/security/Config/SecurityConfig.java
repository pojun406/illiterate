package com.illiterate.illiterate.security.Config;

import com.illiterate.illiterate.security.Filter.CustomLogoutFilter;
import com.illiterate.illiterate.security.Filter.JWTFilter;
import com.illiterate.illiterate.security.Filter.LoginFilter;
import com.illiterate.illiterate.security.Repository.RefreshRepository;
import com.illiterate.illiterate.security.Service.CustomUserDetailsService;
import com.illiterate.illiterate.security.Util.JWTUtil;
import com.illiterate.illiterate.security.Util.MakeCookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // Spring Security를 위한 설정 클래스임을 선언
//@ComponentScan(basePackages = {"com.kyungmin.lavanderia.global.auth.jwt.data.*","com.kyungmin.lavanderia.*"})
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomUserDetailsService customUserDetailsService;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final MakeCookie makeCookie;

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // PasswordEncoder Bean 등록
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 사용자 정의 UserDetailsService 설정
    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 로그인 필터 추가
        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, makeCookie);
        loginFilter.setFilterProcessesUrl("/login"); // 실제 로그인을 처리할 URL을 입력

        http.cors(cors -> cors
                .configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // 허용할 포트
                        configuration.setAllowedMethods(Collections.singletonList("*")); // 모든 메소드 허용
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*")); // 헤더 허용
                        configuration.setMaxAge(3600L);
                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization")); // 헤더 허용
                        return configuration;
                    }
                }));

        // CSRF(Cross-Site Request Forgery) 보호를 비활성화
        http.csrf(csrf -> csrf.disable());

        // 기본 로그인 폼 기반의 인증을 비활성화
        http.formLogin(form -> form.disable());

        // HTTP Basic 인증을 비활성화 -> 보완성이 낮아서 비활성
        http.httpBasic(httpBasic -> httpBasic.disable());

        // 경로별 인가 작업
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/join", "/sendVerificationEmail", "/verify", "/checkId").permitAll() // /login, /, /join, /sendVerificationEmail, /verify 경로는 모든 사용자가 접근할 수 있도록 허용
                .requestMatchers("/refresh").permitAll() // refresh token 재발급 경로는 모든 사용자가 접근할 수 있도록 허용
                .requestMatchers("/admin").hasRole("ADMIN") // /admin 경로는 "ADMIN" 역할을 가진 사용자만 접근할 수 있도록 설정
                .requestMatchers("/ocr/**", "/board/**").authenticated() // OCR 관련 경로는 인증된 사용자만 접근 가능
                .requestMatchers("/userinfo/**", "/userUpdate/**", "/deluser/**").authenticated() // 사용자 정보 조회, 업데이트 및 삭제는 인증된 사용자만 접근 가능
                .anyRequest().authenticated()); // 나머지 모든 요청은 인증된 사용자만 접근 가능

        // JWT 필터 추가
        http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 로그아웃 필터 추가
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        // 로그인 필터 위치 설정
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        // 세션 설정
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}