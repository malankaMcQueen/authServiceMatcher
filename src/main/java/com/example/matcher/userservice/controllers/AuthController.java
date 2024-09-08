package com.example.matcher.userservice.controllers;

import com.example.matcher.userservice.configuration.SecurityConfiguration;
import com.example.matcher.userservice.exception.ResourceNotFoundException;
import com.example.matcher.userservice.model.JwtAuthenticationResponse;
import com.example.matcher.userservice.model.TokenConfirmationEmail;
import com.example.matcher.userservice.model.User;
import com.example.matcher.userservice.repository.TokenConfirmationEmailRepository;
import com.example.matcher.userservice.service.AuthenticationService;
import com.example.matcher.userservice.service.JwtService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/UserService/auth")
//@RequestMapping("/login/oauth2/code")
public class AuthController {

    private AuthenticationService authenticationService;
    private JwtService jwtService;
    private TokenConfirmationEmailRepository token;

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    @GetMapping("/token")
    public List<TokenConfirmationEmail> validateToken() {
        return token.findAll();
    }

    @GetMapping("/google")
    public ResponseEntity<JwtAuthenticationResponse> loginWithGoogle(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User != null) {
            String userEmail = oauth2User.getAttribute("email"); // Email пользователя
            User user = new User();
            user.setEmail(userEmail);
            return new ResponseEntity<>(new JwtAuthenticationResponse(jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user)), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("Error");
        }
    }


    @PostMapping("/registration/email/sendToken")
    public ResponseEntity<String> sendEmailRegistrationToken(@RequestParam("email") String email) {
        authenticationService.sendEmailRegistrationToken(email);
        return new ResponseEntity<>("Check email", HttpStatus.OK);
    }

    @PostMapping("/email/confirmationEmail")
    public ResponseEntity<JwtAuthenticationResponse> confirmationEmail(@RequestParam("email") String email, @RequestParam("token") String token) {
        return new ResponseEntity<>(authenticationService.confirmationEmail(email, token), HttpStatus.OK);
    }

    @PostMapping("/login/email/sendToken")
    public ResponseEntity<String> sendEmailLoginToken(@RequestParam("email") String email) {
        authenticationService.sendEmailLoginToken(email);
        return new ResponseEntity<>("Check email", HttpStatus.OK);
    }

//    @PostMapping("/register")
//    public ResponseEntity<JwtAuthenticationResponse> registerUser(@RequestBody UserDTO userDTO, @RequestParam("token") String token){
//        return new ResponseEntity<>(authenticationService.signUp(userDTO, token), HttpStatus.CREATED);
//    }
}
