package com.illiterate.illiterate.JWT;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 인증 성공 후 처리할 작업을 구현합니다.
        // 예를 들어, 세션 관리, 로그 기록, 사용자 정보 업데이트 등을 수행할 수 있습니다.
        System.out.println("됨?");
        // 인증 성공 후 리다이렉트할 URL을 설정합니다.
        response.sendRedirect("/api/login");
    }
}
