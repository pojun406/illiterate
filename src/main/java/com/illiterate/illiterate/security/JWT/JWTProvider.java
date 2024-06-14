package com.illiterate.illiterate.security.JWT;

import com.illiterate.illiterate.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JWTProvider {
    private final String AUTHENTICATION_CLAIM_NAME = "roles";

    @Value("${jwt.secret-key}")
    private String accessSecret;

    @Value("${jwt.refresh-expiration-seconds}")
    private int refreshExpirationSeconds;

    @Value("${jwt.access-expiration-seconds}")
    private int accessExpirationSeconds;

    /**
     * access 토큰 생성
     */
    public String createAccessToken(UserDetailsImpl userDetails) {
        Instant now = Instant.now();
        Date expiration = Date.from(now.plusSeconds(accessExpirationSeconds));
        SecretKey key = extractSecretKey();

        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        return Jwts.builder()
                .claim("id", userDetails.getId())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(expiration)
                .claim(AUTHENTICATION_CLAIM_NAME, roles)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * refresh 토큰 생성
     */
    public String createRefreshToken(UserDetailsImpl userDetails) {
        Instant now = Instant.now();
        Date expiration = Date.from(now.plusSeconds(refreshExpirationSeconds));
        SecretKey key = extractSecretKey();

        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        return Jwts.builder()
                .claim("id", userDetails.getId())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(expiration)
                .claim(AUTHENTICATION_CLAIM_NAME, roles)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 권한 체크
     */
    public Authentication toAuthentication(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(extractSecretKey())
                .build();
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        String roles = claims.get(AUTHENTICATION_CLAIM_NAME, String.class);
        List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails user = UserDetailsImpl.builder()
                .id(Long.valueOf(claims.get("id", Integer.class)))
                .userid(claims.getSubject())
                .password(null)
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(user, token, authorities);
    }

    /**
     * 토큰 검증
     */
    public boolean validate(String token) {
        try {
            JwtParser jwtParser = Jwts.parserBuilder()
                    .setSigningKey(extractSecretKey())
                    .build();
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Expired JWT token: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("JWT token validation error: " + e.getMessage());
        }
        return false;
    }

    /**
     * SecretKey 추출
     */
    private SecretKey extractSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
    }

    /**
     * 토큰값으로 ID 추출
     */
    public Long getUserIdFromToken(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(extractSecretKey())
                .build();
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claims.get("id", Long.class);
    }
}