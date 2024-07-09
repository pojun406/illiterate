package com.illiterate.illiterate.security.Service;

import com.illiterate.illiterate.common.enums.GlobalErrorCode;
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
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final MakeCookie makeCookie;

    @Override
    public ResponseEntity<BfResponse<?>> reissue(String refreshToken, Long userId, HttpServletRequest request, HttpServletResponse response) {
        if (refreshToken == null || userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BfResponse<>(GlobalSuccessCode.BAD_REQUEST, "refresh token or user id null"));
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BfResponse<>(GlobalSuccessCode.BAD_REQUEST, "refresh token expired"));
        }

        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BfResponse<>(GlobalSuccessCode.BAD_REQUEST, "invalid refresh token"));
        }

        Boolean isExist = refreshRepository.existsByRefresh(refreshToken);
        if (!isExist) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BfResponse<>(GlobalSuccessCode.BAD_REQUEST, "invalid refresh token"));
        }

        Long memberId = jwtUtil.getMemberId(refreshToken);
        if (!memberId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BfResponse<>(GlobalSuccessCode.BAD_REQUEST, "user id mismatch"));
        }

        List<String> role = jwtUtil.getRole(refreshToken);
        String newAccess = jwtUtil.createJwt("access", memberId, role, TokenExpirationTime.ACCESS_TIME);
        String newRefresh = jwtUtil.createJwt("refresh", memberId, role, TokenExpirationTime.REFRESH_TIME);

        refreshRepository.delete(refreshRepository.findByRefresh(refreshToken));

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        jwtUtil.addRefreshEntity(memberId, newRefresh, TokenExpirationTime.REFRESH_TIME, ipAddress);

        response.setHeader("access", newAccess);
        response.addCookie(makeCookie.createCookie("refresh", newRefresh));

        return ResponseEntity.ok(new BfResponse<>(GlobalSuccessCode.SUCCESS, "Token reissued successfully"));
    }
}