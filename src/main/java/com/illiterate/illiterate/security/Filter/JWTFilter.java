package com.illiterate.illiterate.security.Filter;

import com.illiterate.illiterate.member.enums.RolesType;
import com.illiterate.illiterate.security.Util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청에서 토큰을 추출
        String accessToken = resolveToken(request);
        System.out.println("Extracted Token: " + accessToken);  // 디버깅 로그 추가

        // 토큰이 없으면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 토큰의 만료 여부를 확인
            if (jwtUtil.isExpired(accessToken)) {
                handleException(response, "access token expired");
                return;
            }

            // 토큰이 access인지 확인
            String category = jwtUtil.getCategory(accessToken);
            if (!"access".equals(category)) {
                handleException(response, "invalid access token");
                return;
            }

            // 토큰에서 회원 ID와 역할을 추출
            Long memberId = jwtUtil.getMemberId(accessToken);
            String roleStr = jwtUtil.getRole(accessToken).get(0);
            RolesType role = RolesType.valueOf(roleStr);
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());

            // 인증 토큰 생성 및 설정
            Authentication authToken = new UsernamePasswordAuthenticationToken(memberId, null, Collections.singletonList(authority));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (ExpiredJwtException e) {
            // 토큰 만료 예외 처리
            handleException(response, "access token expired");
            return;
        } catch (Exception e) {
            // 기타 예외 처리
            handleException(response, "invalid access token");
            return;
        }

        // 다음 필터로 요청을 넘김
        filterChain.doFilter(request, response);
    }

    // 요청에서 토큰을 추출하는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        System.out.println("Bearer Token: " + bearerToken);  // 로그 추가
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 예외 발생 시 응답을 설정하는 메서드
    private void handleException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"error\": \"" + message + "\"}");
        writer.flush();
    }
}
