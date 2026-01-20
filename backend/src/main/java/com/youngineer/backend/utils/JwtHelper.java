package com.youngineer.backend.utils;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;

import java.nio.charset.StandardCharsets;
import java.rmi.NoSuchObjectException;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class JwtHelper {
    private static final Integer EXPIRATION_MINUTES = 60;
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static final String SECRET_KEY_STRING = dotenv.get("JWT_SECRET_KEY");
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    public static String generateToken(String emailId, Long userId) {
        var now = Instant.now();

        return Jwts.builder()
                .subject(emailId)
                .claim("userId", userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES)))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String extractEmail(String token) {
        return getTokenBody(token).getSubject();
    }

    public static String getEmail(HttpServletRequest request) {
        try {
            if (request.getCookies() != null) {
                for(Cookie cookie: request.getCookies()) {
                    if("token".equals(cookie.getName())) {
                        return extractEmail(cookie.getValue());
                    }
                }
            }
            throw new NoSuchObjectException("Token cookie not found");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Long extractUserId(String token) {
        return getTokenBody(token).get("userId", Long.class);
    }

    public static Long getUserId(HttpServletRequest request) {
        try {
             if (request.getCookies() != null) {
                for(Cookie cookie: request.getCookies()) {
                    if("token".equals(cookie.getName())) {
                        return extractUserId(cookie.getValue());
                    }
                }
            }
            throw new NoSuchObjectException("Token cookie not found");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean validateToken(String token, String email) {
        final String userEmailId = extractEmail(token);
        return userEmailId.equals(email) && !isTokenExpired(token);
    }

    private static Claims getTokenBody(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith((javax.crypto.SecretKey) SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException | ExpiredJwtException e) {
            throw new AccessDeniedException("Access denied: " + e.getMessage());
        }
    }

    private static boolean isTokenExpired(String token) {
        Claims claims = getTokenBody(token);
        return claims.getExpiration().before(new Date());
    }
}
