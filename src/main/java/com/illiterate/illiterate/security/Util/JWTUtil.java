package com.illiterate.illiterate.security.Util;

import com.illiterate.illiterate.security.Entity.RefreshEntity;
import com.illiterate.illiterate.security.Repository.RefreshRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class JWTUtil {

    private final SecretKey secretKey;
    private final RefreshRepository refreshRepository;

    public JWTUtil(@Value("${jwt.secret-key}") String secret, RefreshRepository refreshRepository) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.refreshRepository = refreshRepository;
    }

    public Long getMemberId(String token) {
        return getClaims(token).get("memberId", Long.class);
    }

    public List<String> getRole(String token) {
        String roles = getClaims(token).get("roles", String.class);
        return Arrays.asList(roles.split(","));
    }

    public Boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public String getCategory(String token) {
        return getClaims(token).get("category", String.class);
    }

    public String createJwt(String category, Long memberId, List<String> roles, Long expiredMs) {
        String rolesStr = String.join(",", roles);

        return Jwts.builder()
                .claim("category", category)
                .claim("memberId", memberId)
                .claim("roles", rolesStr)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String generateToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000); // 1시간 유효기간

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public void addRefreshEntity(Long memberId, String refresh, Long expiredMs, String ipAddress) {
        RefreshEntity refreshEntity = RefreshEntity.builder()
                .memberId(memberId + ":" + ipAddress)
                .refresh(refresh)
                .expiration(expiredMs / 1000)
                .build();

        refreshRepository.save(refreshEntity);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        return claims.get("id", Long.class);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(String.valueOf(secretKey)));
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}
