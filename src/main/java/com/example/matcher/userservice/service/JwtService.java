package com.example.matcher.userservice.service;

import com.example.matcher.userservice.exception.InvalidCredentialsException;
import com.example.matcher.userservice.model.JwtAuthenticationResponse;
import com.example.matcher.userservice.model.RefreshToken;
import com.example.matcher.userservice.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

import org.springframework.security.core.authority.SimpleGrantedAuthority;


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
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
                .signWith(getSecretKey(jwtAccessSecret), SignatureAlgorithm.HS256)
                .claim("UUID", user.getId())
                .compact();
    }

    public String generateRefreshToken(@NonNull User user) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
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

    public JwtAuthenticationResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenService.findTokenWithUser(refreshToken).orElseThrow(() ->
                new InvalidCredentialsException("Refresh token not valid"));
        if (!validateToken(refreshToken, getSecretKey(jwtRefreshSecret))) {
            throw new InvalidCredentialsException("Refresh token not valid");
        }
        User user = token.getUser();
        return new JwtAuthenticationResponse(generateAccessToken(user), generateRefreshToken(user));
    }
}