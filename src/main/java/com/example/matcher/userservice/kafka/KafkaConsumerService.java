package com.example.matcher.userservice.kafka;

import com.example.matcher.userservice.aspect.AspectAnnotation;
import com.example.matcher.userservice.repository.RefreshTokenRepository;
import com.example.matcher.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class KafkaConsumerService {


    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    @AspectAnnotation
    @Transactional
    @KafkaListener(topics = "delete_profile", groupId = "${spring.kafka.consumer.group-id}")
    public void listenDeleteProfile(String userId) {
        System.out.println("Received JSON message: " + userId);
        refreshTokenRepository.deleteByUserId(UUID.fromString(userId));
        userRepository.deleteById(UUID.fromString(userId));
    }
}
