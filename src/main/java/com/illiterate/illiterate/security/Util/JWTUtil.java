package com.illiterate.illiterate.security.Util;

import com.kyungmin.lavanderia.global.auth.jwt.data.entity.RefreshEntity;
import com.kyungmin.lavanderia.global.auth.jwt.data.repository.RefreshRepository;
import io.jsonwebtoken.Jwts;
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

    private SecretKey secretKey;
    private final RefreshRepository refreshRepository;


    public JWTUtil(@Value("${spring.jwt.secret}") String secret, RefreshRepository refreshRepository) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.refreshRepository = refreshRepository;
    }

    public String getMemberId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("memberId", String.class);
    }

    public List<String> getRole(String token) {
        String roles = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("roles", String.class);
        return Arrays.asList(roles.split(","));
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public String createJwt(String category, String memberId, List<String> roles, Long expiredMs) {

        String rolesStr = String.join(",", roles);

        return Jwts.builder()
                .claim("category", category)
                .claim("memberId", memberId)
                .claim("roles", rolesStr)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
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
}
