package com.example.financetracker.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.financetracker.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    private static final Logger logger = Logger.getLogger(JwtService.class.getName());

    @Value("${jwt.secret}")
    private String jwtSecretRaw;

    private SecretKey jwtSecret;

    private final long jwtExpirationMs = 86400000;

    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = jwtSecretRaw.getBytes(StandardCharsets.UTF_8);
            jwtSecret = Keys.hmacShaKeyFor(keyBytes);

            jwtParser = Jwts.parser().verifyWith(jwtSecret).build();

            logger.info("JWT Service initialized successfully.");
        } catch (Exception e) {
            logger.severe("Error initializing JWT Service: " + e.getMessage());
            throw new RuntimeException("JWT initialization failed", e);
        }
    }

    public String generateToken(User user) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtExpirationMs))
                .signWith(jwtSecret)
                .compact();
    }

    public String extractUsername(String token) {
        Jwt<JwsHeader, Claims> jwt = jwtParser.parseSignedClaims(token);
        return jwt.getPayload().getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            logger.warning("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Jwt<JwsHeader, Claims> jwt = jwtParser.parseSignedClaims(token);
        return jwt.getPayload().getExpiration().before(new Date());
    }
}
