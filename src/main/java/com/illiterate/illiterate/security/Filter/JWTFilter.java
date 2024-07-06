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
        String accessToken = resolveToken(request);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtUtil.isExpired(accessToken)) {
                handleException(response, "access token expired");
                return;
            }

            String category = jwtUtil.getCategory(accessToken);
            if (!"access".equals(category)) {
                handleException(response, "invalid access token");
                return;
            }

            Long memberId = jwtUtil.getMemberId(accessToken);
            String roleStr = jwtUtil.getRole(accessToken).get(0);
            RolesType role = RolesType.valueOf(roleStr);
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());

            Authentication authToken = new UsernamePasswordAuthenticationToken(memberId, null, Collections.singletonList(authority));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (ExpiredJwtException e) {
            handleException(response, "access token expired");
            return;
        } catch (Exception e) {
            handleException(response, "invalid access token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void handleException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print("{\"error\": \"" + message + "\"}");
        writer.flush();
    }
}
