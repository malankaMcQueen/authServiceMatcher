package com.example.matcher.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "refreshToken")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String token;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
