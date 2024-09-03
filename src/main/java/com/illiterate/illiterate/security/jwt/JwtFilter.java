package com.illiterate.illiterate.security.jwt;

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

import static com.illiterate.illiterate.common.enums.GlobalErrorCode.*;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final String AUTHENTICATION_HEADER = "Authorization";
    private final String AUTHENTICATION_SCHEME = "Bearer ";

    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String accessToken = extractToken(request);
            System.out.println("accessToken : " + accessToken);

            if (hasText(accessToken)) {
                jwtProvider.validate(accessToken);

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(jwtProvider.toAuthentication(accessToken));

                SecurityContextHolder.setContext(context);
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            BaseErrorCode errorCode;

            if (e instanceof ExpiredJwtException) {
                // 토큰 만료
                errorCode = VALIDATION_TOKEN_EXPIRED;
                log.warn(">>>>> ExpiredJwtException : ", e);
            } else if (e instanceof AuthenticationCredentialsNotFoundException) {
                // 유효하지 않은 토큰
                errorCode = VALIDATION_TOKEN_FAILED;
                log.warn(">>>>> AuthenticationCredentialsNotFoundException : ", e);
            } else if (e instanceof AccessDeniedException) {
                // 접근권한이 없음
                errorCode = VALIDATION_TOKEN_NOT_AUTHORIZATION;
                log.warn(">>>>> AccessDeniedException : ", e);
            } else {
                errorCode = VALIDATION_TOKEN_FAILED;
                log.warn(">>>>> TokenException : ", e);
            }

            response.setStatus(errorCode.getStatus().value()); // 상태 코드 설정
            response.setContentType("application/json"); // 응답의 Content-Type 설정
            response.setCharacterEncoding("UTF-8"); // 문자 인코딩 설정

            // json 직렬화
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
        log.debug("Received Authorization header: {}", bearerToken);

        if (hasText(bearerToken) && bearerToken.startsWith(AUTHENTICATION_SCHEME)) {
            return bearerToken.substring(AUTHENTICATION_SCHEME.length());
        }

        throw new AuthenticationCredentialsNotFoundException("토큰이 존재하지 않습니다.");
    }
}
