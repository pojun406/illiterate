package com.illiterate.illiterate.security.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.security.Util.JWTUtil;
import com.illiterate.illiterate.security.Util.MakeCookie;
import com.illiterate.illiterate.security.Util.TokenExpirationTime;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private Long accessExpiredMs = TokenExpirationTime.ACCESS_TIME;
    private Long refreshExpiredMs = TokenExpirationTime.REFRESH_TIME;

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final MakeCookie makeCookie;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            // json으로 로그인
            ObjectMapper objectMapper = new ObjectMapper();
            Member member = objectMapper.readValue(request.getInputStream(), Member.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(member.getUserid(), member.getPassword());
            // AuthenticationManager를 통해 인증 프로세스 시작
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse authentication request body", e);
        }
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        //유저 정보
        String memberId = authentication.getName();

        // 권한 정보 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        //토큰 생성
        String access = jwtUtil.createJwt("access", memberId, roles, accessExpiredMs);
        String refresh = jwtUtil.createJwt("refresh", memberId, roles, refreshExpiredMs);

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        // Refresh 토큰 저장
        jwtUtil.addRefreshEntity(memberId, refresh, refreshExpiredMs, ipAddress);

        //응답 설정
        response.setHeader("access", access);
        response.addCookie(makeCookie.createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }
}
