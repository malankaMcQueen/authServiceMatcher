package com.example.matcher.userservice.service;


import com.example.matcher.userservice.aspect.AspectAnnotation;

import com.example.matcher.userservice.configuration.SecurityConfiguration;
import com.example.matcher.userservice.model.TokenConfirmationEmail;
import com.example.matcher.userservice.repository.TokenConfirmationEmailRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;


@Service
@AllArgsConstructor
public class TokenConfirmationEmailService {

    private final TokenConfirmationEmailRepository tokenConfirmationEmailRepository;
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    //    private final EmailService emailService;
    @Scheduled(fixedRate = 3600000) // Запуск задачи каждые 60 минут (3600000 мс)
    @Transactional
    public void cleanUpExpiredTokens() {
        logger.info("StartCLeanToken");
        tokenConfirmationEmailRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }

    @AspectAnnotation
    public String createToken(String email) {
        Random rnd = new Random(System.currentTimeMillis());
        String token;
        do {
            token = Integer.toString(10000 + rnd.nextInt(99999 - 10000 + 1));      // Рандомное 6ти значное число
        } while (tokenConfirmationEmailRepository.findByToken(token).isPresent());
        TokenConfirmationEmail existingToken = tokenConfirmationEmailRepository.findByEmail(email);
        if (existingToken != null) {
            existingToken.setToken(token);
            existingToken.setExpiryDate(LocalDateTime.now().plusMinutes(60));
            tokenConfirmationEmailRepository.save(existingToken);
        } else {
            TokenConfirmationEmail newToken = new TokenConfirmationEmail();
            newToken.setToken(token);
            newToken.setEmail(email);
            newToken.setExpiryDate(LocalDateTime.now().plusMinutes(60));
            tokenConfirmationEmailRepository.save(newToken);
        }
        return token;
    }


//    @AspectAnnotation
//    public boolean isValidToken(String token, String email) {
//        TokenConfirmationEmail resetToken = tokenConfirmationEmailRepository.findByToken(token).orElseThrow(()
//                -> new ResourceNotFoundException("Token not found"));
//        return resetToken.getEmail().equals(email) && !resetToken.getExpiryDate().isBefore(LocalDateTime.now());
//    }

    @Transactional
    public boolean validateAndDeleteTokenIfPresent(String token, String email) {
        Optional<TokenConfirmationEmail> resetToken = tokenConfirmationEmailRepository.findByToken(token);
        if (resetToken.isEmpty() || !resetToken.get().getEmail().equals(email) || resetToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return false; // Токен недействителен
        }
        tokenConfirmationEmailRepository.delete(resetToken.get()); // Удаляем токен
        return true;
    }

    @AspectAnnotation
    public void deleteToken(String token) {
//        TokenConfirmationEmail tokenConfirmationEmail = tokenConfirmationEmailRepository.findByToken(token).get();
        tokenConfirmationEmailRepository.deleteByToken(token);
    }


}
