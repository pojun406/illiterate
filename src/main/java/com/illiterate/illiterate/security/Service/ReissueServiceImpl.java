package com.illiterate.illiterate.security.Service;

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
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        // refresh 토큰 쿠키에서 추출

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        // refresh 없으면
        if (refresh == null) {

            // refresh 없으면 오류 리턴
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // 유효시간 검사
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            // 유효시간 만료면 오류 리턴
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            // 카테고리가 refresh가 아니면 오류 리턴
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {

            // DB에 없으면 오류 리턴
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        Long memberId = jwtUtil.getMemberId(refresh);
        List<String> role = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", memberId, role, accessExpiredMs);
        String newRefresh = jwtUtil.createJwt("refresh", memberId, role, refreshExpiredMs);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.delete(refreshRepository.findByRefresh(refresh));

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        jwtUtil.addRefreshEntity(memberId, newRefresh, refreshExpiredMs, ipAddress);

        //response
        response.setHeader("access", newAccess);
        response.addCookie(makeCookie.createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
