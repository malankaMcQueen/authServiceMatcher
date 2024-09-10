package com.example.matcher.userservice.repository;

import com.example.matcher.userservice.model.RefreshToken;
import com.example.matcher.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByUser(User user);

    Optional<RefreshToken> findByToken(String token);
}
