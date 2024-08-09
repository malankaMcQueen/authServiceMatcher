package com.example.matcher.userservice.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
@AllArgsConstructor
public class EmailService {

    private JavaMailSender mailSender;

    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendRegistrationConfirmationEmail(String to, String token) {
        String subject = "Регистрация в Matcher";
        String message = "Пожалуйста, введите эти символы для подтверждения почты: " + token;
        sendSimpleEmail(to, subject, message);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Восстановление пароля Matcher";
//        String resetUrl = "http://localhost:8080/resetPassword?token=" + token;
        String message = "Чтобы восстановить пароль, введите эти символы: " + token;

        sendSimpleEmail(to, subject, message);
    }


}

