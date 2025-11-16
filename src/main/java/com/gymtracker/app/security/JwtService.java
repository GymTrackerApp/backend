package com.gymtracker.app.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtService {
    private final Clock clock;
    private final Key signingKey;

    public JwtService(Clock clock, @Value("${jwt.secret.key}") String secretKey) {
        this.clock = clock;
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, String userId) {
        Instant now = Instant.now(clock);
        Instant expiryDate = now.plus(1, ChronoUnit.DAYS);

        return Jwts.builder()
                .claims()
                .add("username", username)
                .subject(userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .and()
                .signWith(signingKey)
                .compact();
    }
}
