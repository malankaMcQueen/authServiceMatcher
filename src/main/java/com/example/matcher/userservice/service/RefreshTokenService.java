package com.example.matcher.userservice.service;

import com.example.matcher.userservice.model.RefreshToken;
import com.example.matcher.userservice.model.User;
import com.example.matcher.userservice.repository.RefreshTokenRepository;
import com.example.matcher.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository;

    public void saveToken(RefreshToken refreshToken) {
//        deleteTokenByUser(refreshToken.getUser());
        refreshTokenRepository.save(refreshToken);
    }
    @Transactional
    public void deleteTokenByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }


    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


}
