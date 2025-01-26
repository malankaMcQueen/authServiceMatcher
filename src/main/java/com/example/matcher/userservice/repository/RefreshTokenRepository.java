package com.example.matcher.userservice.repository;

import com.example.matcher.userservice.model.RefreshToken;
import com.example.matcher.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByUser(User user);

    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(UUID uuid);

    @Query("SELECT r FROM RefreshToken r JOIN FETCH r.user WHERE r.token = :token")
    Optional<RefreshToken> findTokenWithUser(@Param("token") String token);

    @Query("SELECT r FROM RefreshToken r WHERE r.user = :user")
    Optional<RefreshToken> findByUser(User user);
}
