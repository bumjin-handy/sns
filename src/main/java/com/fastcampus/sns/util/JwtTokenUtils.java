package com.fastcampus.sns.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;

public class JwtTokenUtils {

    public static String generateToken(String userName, String key, long expiredTimeMs) {
        return Jwts.builder()
                .claim("userName", userName)
                // Fri Jun 24 2016 15:33:42 GMT-0400 (EDT)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Sat Jun 24 2116 15:33:42 GMT-0400 (EDT)
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
                .signWith(getKey(key),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    private static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
