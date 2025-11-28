// src/main/java/com/example/backend/security/JwtService.java
package com.example.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final Key accessKey;
    private final Key refreshKey;
    private final long accessExpMs;
    private final long refreshExpMs;

    public JwtService(
            @Value("${security.jwt.secret}") String accessSecret,
            @Value("${security.jwt.expiration-ms:900000}") long accessExpMs,
            @Value("${security.refresh.secret:${JWT_SECRET}}") String refreshSecret,
            @Value("${security.refresh.expiration-ms:2592000000}") long refreshExpMs
    ) {
        if (accessSecret == null || accessSecret.length() < 32)
            throw new IllegalStateException("security.jwt.secret debe tener >= 32 caracteres");
        if (refreshSecret == null || refreshSecret.length() < 32)
            throw new IllegalStateException("security.refresh.secret/JWT_SECRET debe tener >= 32 caracteres");

        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMs = accessExpMs;
        this.refreshExpMs = refreshExpMs;
    }

    // ===== ACCESS =====
    public String generateToken(UserDetails user) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getAuthorities().iterator().next().getAuthority())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessExpMs))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String accessToken) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(accessKey).build()
                .parseClaimsJws(accessToken).getBody().getSubject();
    }

    public boolean isAccessValid(String token, UserDetails user) {
        var claims = Jwts.parserBuilder().setSigningKey(accessKey).build()
                .parseClaimsJws(token).getBody();
        return user.getUsername().equals(claims.getSubject()) && claims.getExpiration().after(new Date());
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpMs))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsernameFromRefresh(String refreshToken) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(refreshKey).build()
                .parseClaimsJws(refreshToken).getBody().getSubject();
    }

    public boolean isRefreshValid(String token, String expectedUsername) {
        var claims = Jwts.parserBuilder().setSigningKey(refreshKey).build()
                .parseClaimsJws(token).getBody();
        return "refresh".equals(claims.get("type"))
                && expectedUsername.equals(claims.getSubject())
                && claims.getExpiration().after(new Date());
    }

    private Claims parseAccessClaims(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(accessKey).build()
                .parseClaimsJws(token).getBody();
    }

    public Object extractClaim(String token, String name) throws JwtException {
        return parseAccessClaims(token).get(name);
    }

}
