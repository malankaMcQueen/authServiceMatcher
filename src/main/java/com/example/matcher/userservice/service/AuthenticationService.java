package com.example.matcher.userservice.service;

import com.example.matcher.userservice.dto.UserDTO;
import com.example.matcher.userservice.exception.InvalidCredentialsException;
import com.example.matcher.userservice.exception.UserAlreadyExistException;
import com.example.matcher.userservice.model.JwtAuthenticationResponse;
import com.example.matcher.userservice.model.Role;
import com.example.matcher.userservice.model.TokenConfirmationEmail;
import com.example.matcher.userservice.model.User;
import com.example.matcher.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;
    private final TokenConfirmationEmailService tokenConfirmationEmailService;
    private final EmailService emailService;

    public JwtAuthenticationResponse signUp(UserDTO userDTO, String token) {
        if (!tokenConfirmationEmailService.isValidToken(token)) {
            throw new InvalidCredentialsException("Invalid token");
        }
        tokenConfirmationEmailService.deleteToken(token);
        var jwt = jwtService.generateToken(userService.registerUser(userDTO));
        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(UserDTO userDTO) {
        User user = userService.getByEmail(userDTO.getEmail());
        System.out.println(user);
        if (user == null || !passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    public void sendTokenConfirmationEmail(String email){
        if (userService.getByEmail(email) != null) {
            throw new UserAlreadyExistException("User with this email already exists");
        }
        String token = tokenConfirmationEmailService.createToken();
        emailService.sendRegistrationConfirmationEmail(email, token);
    }
}
