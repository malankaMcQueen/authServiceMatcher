package com.example.matcher.userservice.configuration.filter;

import com.example.matcher.userservice.service.JwtService;
import io.jsonwebtoken.io.IOException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException, java.io.IOException {
        String jwt = extractTokenFromHeader(request);

        // Проверка на валидность токена
        if (jwt != null && jwtService.validateAccessToken(jwt)) {
            // Извлечение имени пользователя из токена
            String userId = jwtService.extractAccessClaim(jwt, "UUID", String.class);

            // Создание объекта аутентификации
//            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                    userId,
//                    null,
//                    null // Без указания ролей и привилегий
//            );
//
//            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authToken);

            SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(userId));
        }
        filterChain.doFilter(request, response);
    }

//    protected void doFilterInternal2(HttpServletRequest request, javax.servlet.http.HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        // Извлечение токена из заголовка Authorization
//        String token = extractTokenFromHeader(request);
//
//        if (token != null && jwtService.validateAccessToken(token)) {
//            // Если токен валидный, устанавливаем аутентификацию в контексте
//            String userId = jwtService.extractAccessClaim(token, "UUID", String.class);
//            // Можно использовать userId для создания UserDetails и аутентификации, если нужно
//            SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(userId));
//        }
//
//        filterChain.doFilter(request, response);
//    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authHeader.substring(BEARER_PREFIX.length());
    }
}
