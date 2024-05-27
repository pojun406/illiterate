package com.illiterate.illiterate.JWT;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    @RequestMapping("/api/onAuth")
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 인증 성공 후 처리할 작업을 구현합니다.
        // 예를 들어, 세션 관리, 로그 기록, 사용자 정보 업데이트 등을 수행할 수 있습니다.
        System.out.println("인증");
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        // 사용자가 권한이 필요한 자원에 접근해 인증 예외가 발생해 인증을 처리하는 것이 아닌 경우
        // SavedRequest 객체가 생성되지 않는다.
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request, response, targetUrl);
        } else {
            redirectStrategy.sendRedirect(request, response, "/main.tsx");
        }
        // 인증 성공 후 리다이렉트할 URL을 설정합니다.
        response.sendRedirect("/main.tsx");
    }
}
