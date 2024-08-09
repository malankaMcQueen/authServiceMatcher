package com.example.matcher.userservice.repository;

import com.example.matcher.userservice.model.PasswordResetToken;
import com.example.matcher.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Query("SELECT p.user FROM PasswordResetToken p WHERE p.token = :token")
    Optional<User> findUserByToken(@Param("token") String token);

    void deleteByToken(String token);
}
