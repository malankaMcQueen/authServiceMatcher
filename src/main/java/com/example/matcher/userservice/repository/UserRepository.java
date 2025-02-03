package com.example.matcher.userservice.repository;

import com.example.matcher.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    User findByEmail(String email);

    User findByTelegramId(Long userTelegramId);
}
