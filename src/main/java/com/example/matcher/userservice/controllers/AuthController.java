package com.example.matcher.userservice.controllers;

import com.example.matcher.userservice.configuration.SecurityConfiguration;
import com.example.matcher.userservice.dto.UserDTO;
import com.example.matcher.userservice.model.JwtAuthenticationResponse;
import com.example.matcher.userservice.model.User;
import com.example.matcher.userservice.service.AuthenticationService;
import com.example.matcher.userservice.service.TokenConfirmationEmailService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/UserService/v1/auth")
//@RequestMapping("/login/oauth2/code")
public class AuthController {

    private AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    @GetMapping
    public String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().toString();
    }
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> loginUser(@RequestBody UserDTO userDTO){
        return new ResponseEntity<>(authenticationService.signIn(userDTO), HttpStatus.OK);
    }

    @GetMapping("/google")
    public ResponseEntity<String> loginWithGoogle(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User != null) {
            String userName = oauth2User.getAttribute("name");  // Имя пользователя
            String userEmail = oauth2User.getAttribute("email"); // Email пользователя
            return new ResponseEntity<>(userEmail + " + " + userName, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("OAuth2User is null", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/emailConfirmation/sendToken")
    public ResponseEntity<String> generateEmailToken(@RequestParam("email") String email) {
        authenticationService.sendTokenConfirmationEmail(email);
        return new ResponseEntity<>("Check email", HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> registerUser(@RequestBody UserDTO userDTO){
        return new ResponseEntity<>(authenticationService.registration(userDTO), HttpStatus.CREATED);
    }
//    @PostMapping("/register")
//    public ResponseEntity<JwtAuthenticationResponse> registerUser(@RequestBody UserDTO userDTO, @RequestParam("token") String token){
//        return new ResponseEntity<>(authenticationService.signUp(userDTO, token), HttpStatus.CREATED);
//    }
}
