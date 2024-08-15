package com.example.matcher.userservice.kafka;

import com.example.matcher.userservice.aspect.AspectAnnotation;
import com.example.matcher.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class KafkaConsumerService {


    private final UserRepository userRepository;
    @AspectAnnotation
    @KafkaListener(topics = "delete_profile", groupId = "${spring.kafka.consumer.group-id}")
    public void listenDeleteProfile(String userId) {
        userRepository.deleteById(UUID.fromString(userId));
    }
}
