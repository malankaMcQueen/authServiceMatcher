package com.example.matcher.userservice.service;

import com.example.matcher.userservice.configuration.SecurityConfiguration;
import com.example.matcher.userservice.model.RefreshToken;
import com.example.matcher.userservice.model.User;
import com.example.matcher.userservice.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@AllArgsConstructor
public class RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    public void saveToken(RefreshToken refreshToken) {
        refreshTokenRepository.findByUser(refreshToken.getUser()).ifPresent(
                token -> refreshToken.setId(token.getId())
        );
        refreshTokenRepository.save(refreshToken);
    }


    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public Optional<RefreshToken> findTokenWithUser(String token) {
        return refreshTokenRepository.findTokenWithUser(token);
    }
}
