package com.example.matcher.userservice.configuration.filter;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final String userId;

    public JwtAuthentication(String userId) {
        super(null);
        this.userId = userId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null; // Можно вернуть какие-то данные, если они нужны
    }

    @Override
    public Object getPrincipal() {
        return userId; // Возвращаем userId из токена
    }
}
