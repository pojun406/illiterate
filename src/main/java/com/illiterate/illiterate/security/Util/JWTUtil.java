package com.illiterate.illiterate.security.Util;

import com.illiterate.illiterate.security.Entity.RefreshEntity;
import com.illiterate.illiterate.security.Repository.RefreshRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
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

    public String getMemberId(String token) {
        return getClaims(token).get("memberId", String.class);
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

    public String createJwt(String category, String memberId, List<String> roles, Long expiredMs) {
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

    public void addRefreshEntity(String memberId, String refresh, Long expiredMs, String ipAddress) {
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
}
