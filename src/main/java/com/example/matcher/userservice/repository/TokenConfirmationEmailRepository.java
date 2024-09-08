package com.example.matcher.userservice.repository;

import com.example.matcher.userservice.model.TokenConfirmationEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenConfirmationEmailRepository extends JpaRepository<TokenConfirmationEmail, Long> {
    void deleteByExpiryDateBefore(LocalDateTime now);

    Optional<TokenConfirmationEmail> findByToken(String token);

    void deleteByToken(String token);

    void deleteByEmail(String email);
}
