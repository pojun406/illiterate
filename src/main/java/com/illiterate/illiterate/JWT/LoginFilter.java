package com.illiterate.illiterate.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private Long accessExpiredMs = 1L;
    private Long refreshExpiredMs = 86400L;

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final MakeCookie makeCookie;


    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, MakeCookie makeCookie) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.makeCookie = makeCookie;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String userid = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println("username : " + userid);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userid, password, null);
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        //유저 정보
        String memberId = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //토큰 생성
        String access = jwtUtil.createJwt("access", memberId, role, accessExpiredMs);
        String refresh = jwtUtil.createJwt("refresh", memberId, role, refreshExpiredMs);

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        //응답 설정
        response.setHeader("access", access);
        response.addCookie(makeCookie.createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }
}
