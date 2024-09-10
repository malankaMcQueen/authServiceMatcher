package com.example.matcher.userservice.service;

import com.example.matcher.userservice.dto.UserDTO;
import com.example.matcher.userservice.exception.InvalidCredentialsException;
import com.example.matcher.userservice.exception.ResourceNotFoundException;
import com.example.matcher.userservice.exception.TokenExpiredException;
import com.example.matcher.userservice.exception.UserAlreadyExistException;
import com.example.matcher.userservice.model.JwtAuthenticationResponse;
import com.example.matcher.userservice.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final TokenConfirmationEmailService tokenConfirmationEmailService;
    private final EmailService emailService;

    public void sendEmailRegistrationToken(String email){
        if (userService.getByEmail(email) != null) {
            throw new UserAlreadyExistException("User with this email already exists");
        }
        String token = tokenConfirmationEmailService.createToken(email);
        emailService.sendRegistrationConfirmationEmail(email, token);
    }

    public void sendEmailLoginToken(String email){
        if (userService.getByEmail(email) == null) {
            throw new ResourceNotFoundException("User with this email dont exists");
        }
        String token = tokenConfirmationEmailService.createToken(email);
        emailService.sendRegistrationConfirmationEmail(email, token);
    }

    public JwtAuthenticationResponse confirmationEmail(String email, String token) {
        if (!tokenConfirmationEmailService.isValidToken(token, email)) {
            throw new TokenExpiredException("Invalid token");
        }
        tokenConfirmationEmailService.deleteToken(token);
        User user = userService.getByEmail(email);
        if (user == null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(email);
            user = userService.registerUser(userDTO);
        }
        return new JwtAuthenticationResponse(jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user));

    }
}
