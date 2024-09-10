package com.example.matcher.userservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "refreshToken")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String token;
    @OneToOne(/*fetch = FetchType.LAZY*/)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
