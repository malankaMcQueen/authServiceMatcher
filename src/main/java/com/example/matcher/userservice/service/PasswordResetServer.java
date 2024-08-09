package com.example.matcher.userservice.service;

import com.example.matcher.userservice.aspect.AspectAnnotation;
import com.example.matcher.userservice.dto.RecoverPasswordDTO;
import com.example.matcher.userservice.dto.ResetPasswordDTO;
import com.example.matcher.userservice.exception.EmailNotFoundException;
import com.example.matcher.userservice.exception.InvalidCredentialsException;
import com.example.matcher.userservice.exception.ResourceNotFoundException;
import com.example.matcher.userservice.exception.TokenExpiredException;
import com.example.matcher.userservice.model.PasswordResetToken;
import com.example.matcher.userservice.model.User;
import com.example.matcher.userservice.repository.PasswordResetTokenRepository;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PasswordResetServer {

    private PasswordResetTokenRepository tokenRepository;
    private UserService userService;
    private EmailService emailService;

    @AspectAnnotation
    public String createToken(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        resetToken.setUser(user);
        tokenRepository.save(resetToken);
        return token;
    }

    @AspectAnnotation
    public boolean isValidToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token).orElseThrow(()
                -> new ResourceNotFoundException("Token not found"));

        //            throw new TokenExpiredException("Token has expired");
        return !resetToken.getExpiryDate().isBefore(LocalDateTime.now());
    }


//    public User getUserByToken(String token) {
//        PasswordResetToken resetToken = tokenRepository.findByToken(token);
//        return resetToken != null ? resetToken.getUser() : null;
//    }

//    public void invalidateToken(String token) {
//        tokenRepository.deleteByToken(token);
//    }

    @AspectAnnotation
    public String emailConfirmation(RecoverPasswordDTO recoverPasswordDTO) {
        User user = userService.getByEmail(recoverPasswordDTO.getEmail());
        if (user == null) {
            throw new EmailNotFoundException("Email not found");
        }
        String token = this.createToken(user);
        emailService.sendPasswordResetEmail(user.getEmail(), token);
        return "Token send on email: " + recoverPasswordDTO.getEmail() + " Token: " + token;
    }

    @AspectAnnotation
    public String resetPassword(ResetPasswordDTO resetPasswordDTO) {
//        User = tokenRepository.findUserByToken(resetPasswordDTO.getToken()).orElseThrow()
//    -> new ;
        if (!this.isValidToken(resetPasswordDTO.getToken())){
            throw new InvalidCredentialsException("Invalid token");
        }
        User user = tokenRepository.findUserByToken(resetPasswordDTO.getToken()).orElseThrow(()
                -> new ResourceNotFoundException("User dont found"));
        userService.passwordChange(user, resetPasswordDTO.getPassword());
        return "Success";
    }
}

