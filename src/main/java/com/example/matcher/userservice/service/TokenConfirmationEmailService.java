package com.example.matcher.userservice.service;


import com.example.matcher.userservice.aspect.AspectAnnotation;

import com.example.matcher.userservice.configuration.SecurityConfiguration;
import com.example.matcher.userservice.exception.ResourceNotFoundException;
import com.example.matcher.userservice.model.TokenConfirmationEmail;
import com.example.matcher.userservice.repository.TokenConfirmationEmailRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;


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
//        String token = UUID.randomUUID().toString();
        tokenConfirmationEmailRepository.deleteByEmail(email);
        Random rnd = new Random(System.currentTimeMillis());
        String token;
        do {
            token = Integer.toString(10000 + rnd.nextInt(99999 - 10000 + 1));      // Рандомное 6ти значное число
        } while (tokenConfirmationEmailRepository.findByToken(token).isPresent());
        TokenConfirmationEmail tokenConfirmationEmail = new TokenConfirmationEmail();
        tokenConfirmationEmail.setToken(token);
        tokenConfirmationEmail.setEmail(email);
        tokenConfirmationEmail.setExpiryDate(LocalDateTime.now().plusMinutes(60));
        tokenConfirmationEmailRepository.save(tokenConfirmationEmail);
        return token;
    }


    @AspectAnnotation
    public boolean isValidToken(String token, String email) {
        for (TokenConfirmationEmail tokenConfirmationEmail: tokenConfirmationEmailRepository.findAll()) {
            logger.info(tokenConfirmationEmail.getToken());
        }
        logger.info("All token...");
        TokenConfirmationEmail resetToken = tokenConfirmationEmailRepository.findByToken(token).orElseThrow(()
                -> new ResourceNotFoundException("Token not found"));
        boolean tokenValid = false;
        if (resetToken.getEmail().equals(email)) {
            logger.info("Email equals");
            if (!resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                tokenValid = true;
            }
        }
        else {
            logger.info("BD: " + resetToken.getEmail() + " email - "+email);
        }
        return tokenValid;
//        return resetToken.getEmail().equals(email) && !resetToken.getExpiryDate().isBefore(LocalDateTime.now());
    }

    @AspectAnnotation
    @Transactional
    public void deleteToken(String token) {
//        TokenConfirmationEmail tokenConfirmationEmail = tokenConfirmationEmailRepository.findByToken(token).get();
        tokenConfirmationEmailRepository.deleteByToken(token);
    }

//    @AspectAnnotation
//    public String emailConfirmation(String email) {
//        String token = this.createToken();
//        emailService.sendRegistrationConfirmationEmail(email, token);
//        return "Token send on email: " + email + " Token: " + token;
//    }

//    @AspectAnnotation
//    public String resetPassword(ResetPasswordDTO resetPasswordDTO) {
////        User = tokenRepository.findUserByToken(resetPasswordDTO.getToken()).orElseThrow()
////    -> new ;
//        if (!this.isValidToken(resetPasswordDTO.getToken())){
//            throw new InvalidCredentialsException("Invalid token");
//        }
//        User user = tokenRepository.findUserByToken(resetPasswordDTO.getToken()).orElseThrow(()
//                -> new ResourceNotFoundException("User dont found"));
//        userService.passwordChange(user, resetPasswordDTO.getPassword());
//        return "Success";
//    }

}
