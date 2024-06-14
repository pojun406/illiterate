package com.illiterate.illiterate.security.JWT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illiterate.illiterate.common.enums.BaseErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

import static com.illiterate.illiterate.common.enums.GlobalErrorCode.*;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {
    private final String AUTHENTICATION_HEADER = "Authorization";
    private final String AUTHENTICATION_SCHEME = "Bearer ";

    private final JWTProvider jwtProvider;

    public JWTFilter(JWTProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String accessToken = extractToken(request);
            if (hasText(accessToken)) {
                if (jwtProvider.validate(accessToken)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(jwtProvider.toAuthentication(accessToken));
                    SecurityContextHolder.setContext(context);
                } else {
                    throw new ExpiredJwtException(null, null, "Expired JWT token");
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: ", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
            String responseMessage = objectMapper.writeValueAsString(Map.of("error", "Expired JWT token"));
            response.getWriter().write(responseMessage);
        } catch (Exception e) {
            BaseErrorCode errorCode = VALIDATION_TOKEN_FAILED;
            log.warn("Token validation error: ", e);

            response.setStatus(errorCode.getStatus().value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
            String responseMessage = objectMapper.writeValueAsString(errorCode.getErrorResponse());
            response.getWriter().write(responseMessage);
        }
    }

    /**
     * 토큰 추출
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHENTICATION_HEADER);
        if (hasText(bearerToken) && bearerToken.startsWith(AUTHENTICATION_SCHEME)) {
            return bearerToken.substring(AUTHENTICATION_SCHEME.length());
        }
        throw new AuthenticationCredentialsNotFoundException("토큰이 존재하지 않습니다.");
    }
}
