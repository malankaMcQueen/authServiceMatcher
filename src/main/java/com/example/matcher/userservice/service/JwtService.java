package com.example.matcher.userservice.service;

import com.example.matcher.userservice.exception.InvalidCredentialsException;
import com.example.matcher.userservice.model.JwtAuthenticationResponse;
import com.example.matcher.userservice.model.RefreshToken;
import com.example.matcher.userservice.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.*;


@Slf4j
@Component
public class JwtService {

    @Value("${jwt.secret.access}")
    private String jwtAccessSecret;


    @Value("${jwt.secret.refresh}")
    private String jwtRefreshSecret;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public String generateAccessToken(@NonNull User user) {
        long expirationTimeInMillis = Duration.ofMinutes(15).toMillis();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMillis))
                .signWith(getSecretKey(jwtAccessSecret), SignatureAlgorithm.HS256)
                .claim("UUID", user.getId())
                .compact();
    }

    public String generateRefreshToken(@NonNull User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        long expirationTimeInMillis = Duration.ofHours(5).toMillis();

        refreshToken.setToken(Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMillis))
                .signWith(getSecretKey(jwtRefreshSecret))
                .compact());

        refreshTokenService.saveToken(refreshToken);
        return refreshToken.getToken();
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, getSecretKey(jwtAccessSecret));
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, getSecretKey(jwtRefreshSecret))
                && refreshTokenService.findByToken(refreshToken).isPresent();
    }

    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }

    private Key getSecretKey(String jwtSecret) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtAuthenticationResponse updateAccessAndRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenService.findTokenWithUser(refreshToken).orElseThrow(() ->
                new InvalidCredentialsException("Refresh token not valid"));
        if (!validateToken(refreshToken, getSecretKey(jwtRefreshSecret))) {
            throw new InvalidCredentialsException("Refresh token not valid");
        }
        User user = token.getUser();
        return new JwtAuthenticationResponse(generateAccessToken(user), generateRefreshToken(user));
    }

    public String extractAccessSubject(@NonNull String token) {
        Claims claims = getClaims(token, getSecretKey(jwtAccessSecret));
        return claims.getSubject();
    }
    public String extractRefreshSubject(@NonNull String token) {
        Claims claims = getClaims(token, getSecretKey(jwtRefreshSecret));
        return claims.getSubject();
    }

    public <T> T extractAccessClaim(@NonNull String token, @NonNull String claimKey, @NonNull Class<T> claimType) {
        Claims claims = getClaims(token, getSecretKey(jwtAccessSecret));
        return claims.get(claimKey, claimType); // Извлечение произвольного claim
    }
    public <T> T extractRefreshClaim(@NonNull String token, @NonNull String claimKey, @NonNull Class<T> claimType) {
        Claims claims = getClaims(token, getSecretKey(jwtRefreshSecret));
        return claims.get(claimKey, claimType); // Извлечение произвольного claim
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims extractAllClaims(@NonNull String token, boolean isAccessToken) {
        String secret = isAccessToken ? jwtAccessSecret : jwtRefreshSecret;
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}