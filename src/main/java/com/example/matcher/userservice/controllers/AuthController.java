package com.example.matcher.userservice.controllers;

import com.example.matcher.userservice.dto.UserDTO;
import com.example.matcher.userservice.model.JwtAuthenticationResponse;
import com.example.matcher.userservice.model.User;
import com.example.matcher.userservice.service.AuthenticationService;
import com.example.matcher.userservice.service.TokenConfirmationEmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> loginUser(@RequestBody UserDTO userDTO){
        return new ResponseEntity<>(authenticationService.signIn(userDTO), HttpStatus.OK);
    }

    @PostMapping("/emailConfirmation/sendToken")
    public ResponseEntity<String> generateEmailToken(@RequestParam("email") String email) {
        authenticationService.sendTokenConfirmationEmail(email);
        return new ResponseEntity<>("Check email", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> registerUser(@RequestBody UserDTO userDTO, @RequestParam("token") String token){
        return new ResponseEntity<>(authenticationService.signUp(userDTO, token), HttpStatus.CREATED);
    }
}
