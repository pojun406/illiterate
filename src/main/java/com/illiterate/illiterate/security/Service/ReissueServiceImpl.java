package com.illiterate.illiterate.security.Service;

import com.illiterate.illiterate.common.enums.GlobalSuccessCode;
import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.security.Repository.RefreshRepository;
import com.illiterate.illiterate.security.Util.JWTUtil;
import com.illiterate.illiterate.security.Util.MakeCookie;
import com.illiterate.illiterate.security.Util.TokenExpirationTime;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReissueServiceImpl implements ReissueService {
    private Long accessExpiredMs = TokenExpirationTime.ACCESS_TIME;
    private Long refreshExpiredMs = TokenExpirationTime.REFRESH_TIME;

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final MakeCookie makeCookie;

    @Override
    public ResponseEntity<BfResponse<?>> reissue(HttpServletRequest request, HttpServletResponse response) {

        // refresh 토큰 쿠키에서 추출
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        // refresh 없으면 오류 리턴
        if (refresh == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BfResponse<>(null, "refresh token null"));
        }

        // 유효시간 검사
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BfResponse<>(null, "refresh token expired"));
        }

        // 토큰이 refresh인지 확인
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BfResponse<>(null, "invalid refresh token"));
        }

        // DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BfResponse<>(null, "invalid refresh token"));
        }

        Long memberId = jwtUtil.getMemberId(refresh);
        List<String> role = jwtUtil.getRole(refresh);

        // 새로운 JWT 생성
        String newAccess = jwtUtil.createJwt("access", memberId, role, accessExpiredMs);
        String newRefresh = jwtUtil.createJwt("refresh", memberId, role, refreshExpiredMs);

        // 기존 Refresh 토큰 삭제 후 새 토큰 저장
        refreshRepository.delete(refreshRepository.findByRefresh(refresh));

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        jwtUtil.addRefreshEntity(memberId, newRefresh, refreshExpiredMs, ipAddress);

        // 응답 설정
        response.setHeader("access", newAccess);
        response.addCookie(makeCookie.createCookie("refresh", newRefresh));

        return ResponseEntity.ok(new BfResponse<>(GlobalSuccessCode.SUCCESS, "Token reissued successfully"));
    }
}
