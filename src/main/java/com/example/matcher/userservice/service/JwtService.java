package com.example.matcher.userservice.service;

import com.example.matcher.userservice.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Slf4j
@Component
//@AllArgsConstructor
//@Service
public class JwtService {

//    @Value("${jwt.secret.access}")
    @Value("${jwt.secret.access}")
    private String jwtAccessSecret;


    @Value("${jwt.secret.refresh}")
    private String jwtRefreshSecret;
    
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
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
                .signWith(getSecretKey(jwtRefreshSecret))
                .compact();
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken,getSecretKey(jwtAccessSecret));
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, getSecretKey(jwtRefreshSecret));
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

//    public Claims getAccessClaims(@NonNull String token) {
//        return getClaims(token, getSecretKey(jwtAccessSecret));
//    }
//
//    public Claims getRefreshClaims(@NonNull String token) {
//        return getClaims(token,getSecretKey(jwtRefreshSecret));
//    }
//
//    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
//        return Jwts.parserBuilder()
//                .setSigningKey(secret)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }

    private Key getSecretKey(String jwtSecret) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}

//@Service
//public class JwtService {
//    @Value("${token.signing.key}")
//    private String jwtSigningKey;
//    /**
//     * Извлечение имени пользователя из токена
//     *
//     * @param token токен
//     * @return имя пользователя
//     */
//    public String extractUserName(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//    public List<GrantedAuthority> extractAuthorities(String token) {
//        Claims claims = extractAllClaims(token);
//        String role = claims.get("role", String.class); // Извлечение роли как строки
//
//        // Преобразование строки роли в список SimpleGrantedAuthority
//        return List.of(new SimpleGrantedAuthority(role));
//    }
//    /**
//     * Генерация токена
//     *
//     * @param userDetails данные пользователя
//     * @return токен
//     */
//    public String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//        if (userDetails instanceof User customUserDetails) {
//            claims.put("id", customUserDetails.getId());
////            claims.put("email", customUserDetails.getEmail());
////            claims.put("role", customUserDetails.getRole());
//        }
//        return generateToken(claims, userDetails);
//    }
//    /**
//     * Проверка токена на валидность
//     *
//     * @param token       токен
//     * @param userDetails данные пользователя
//     * @return true, если токен валиден
//     */
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        final String userName = extractUserName(token);
//        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
//    }
//        /**
//     * Проверка легитимности токена без привязки к пользователю
//     *
//     * @param token токен
//     * @return true, если токен валиден
//     */
//    public boolean isTokenValid(String token) {
//        try {
//            return !isTokenExpired(token) && isSignatureValid(token);
//        } catch (JwtException | IllegalArgumentException e) {
//            return false;
//        }
//    }
//
//    /**
//     * Проверка подписи токена
//     *
//     * @param token токен
//     * @return true, если подпись валидна
//     */
//    private boolean isSignatureValid(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
//            return true;
//        } catch (JwtException e) {
//            return false;
//        }
//    }
//    /**
//     * Извлечение данных из токена
//     *
//     * @param token           токен
//     * @param claimsResolvers функция извлечения данных
//     * @param <T>             тип данных
//     * @return данные
//     */
//    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolvers.apply(claims);
//    }
//
//    /**
//     * Генерация токена
//     *
//     * @param extraClaims дополнительные данные
//     * @param userDetails данные пользователя
//     * @return токен
//     */
//    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
//        return Jwts.builder()
//                .setClaims(extraClaims)
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//    /**
//     * Проверка токена на просроченность
//     *
//     * @param token токен
//     * @return true, если токен просрочен
//     */
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    /**
//     * Извлечение даты истечения токена
//     *
//     * @param token токен
//     * @return дата истечения
//     */
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    /**
//     * Извлечение всех данных из токена
//     *
//     * @param token токен
//     * @return данные
//     */
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//    /**
//     * Получение ключа для подписи токена
//     *
//     * @return ключ
//     */
//    private Key getSigningKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//}
