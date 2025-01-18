package com.example.matcher.userservice.controllers;

import com.example.matcher.userservice.aspect.AspectAnnotation;
import com.example.matcher.userservice.exception.ResourceNotFoundException;
import com.example.matcher.userservice.model.JwtAuthenticationResponse;
import com.example.matcher.userservice.model.RefreshToken;
import com.example.matcher.userservice.model.User;
import com.example.matcher.userservice.repository.RefreshTokenRepository;
import com.example.matcher.userservice.service.AuthenticationService;
import com.example.matcher.userservice.service.JwtService;
import com.example.matcher.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class AuthController {

    private AuthenticationService authenticationService;
    private JwtService jwtService;
    private RefreshTokenRepository refreshTokenRepository;
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Operation(summary = "Обновить токен доступа",
            description = "Метод обновляет токен доступа и возвращает новый набор токенов")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "401", description = "Токен не валиден", content = @Content())
    @PostMapping("/updateRefreshToken")
    public ResponseEntity<JwtAuthenticationResponse> updateAccessAndRefreshToken(@RequestParam("refreshToken") String refreshToken) {
        return new ResponseEntity<>(jwtService.updateAccessAndRefreshToken(refreshToken), HttpStatus.OK);
    }

    @Hidden     // Hide in swagger documentation
    @GetMapping("/refreshToken/getAll")
    public ResponseEntity<List<RefreshToken>> getAllRefreshToken() {
        return new ResponseEntity<>(refreshTokenRepository.findAll(), HttpStatus.OK);
    }

    @Hidden     // Hide in swagger documentation
    @AspectAnnotation
    @GetMapping("/google")
    public ResponseEntity<JwtAuthenticationResponse> loginWithGoogle(@AuthenticationPrincipal OAuth2User oauth2User) {

        if (oauth2User != null) {
            String userEmail = oauth2User.getAttribute("email"); // Email пользователя
            User user = userService.getByEmail(userEmail);

            logger.info("Email: " + user.getEmail());
            return new ResponseEntity<>(new JwtAuthenticationResponse(jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user)), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("Error");
        }
    }


    @Operation(summary = "Ввод емайла при регистрации для получения кода",
            description = "Метод отправляет код подтверждения на указанный емайл ПРИ РЕГИСТРАЦИИ!")
    @ApiResponse(responseCode = "200", description = "Успешный ответ", content = @Content())
    @ApiResponse(responseCode = "409", description = "Пользователь с такой почтой уже зарегестрирован", content = @Content())
    @AspectAnnotation
    @PostMapping("/registration/email/sendToken")
    public ResponseEntity<String> sendEmailRegistrationToken(@RequestParam("email") String email) {
        authenticationService.sendEmailRegistrationToken(email);
        return new ResponseEntity<>("Check email", HttpStatus.OK);
    }

    @Operation(summary = "Ввод емайла при авторизации для получения кода",
            description = "Метод отправляет код подтверждения на указанный емайл ПРИ АВТОРИЗАЦИИ!")
    @ApiResponse(responseCode = "200", description = "Успешный ответ", content = @Content())
    @ApiResponse(responseCode = "404", description = "Пользователь с такой почтой не найден", content = @Content())
    @AspectAnnotation
    @PostMapping("/login/email/sendToken")
    public ResponseEntity<String> sendEmailLoginToken(@RequestParam("email") String email) {
        authenticationService.sendEmailLoginToken(email);
        return new ResponseEntity<>("Check email", HttpStatus.OK);
    }

    @Operation(summary = "Ввод кода отправленного на почту",
            description = "Метод сверяет код отправленный на почту и введённый, после чего возвращает набор токенов!")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "401", description = "Токен не валиден", content = @Content())
    @PostMapping("/email/confirmationEmail")
    public ResponseEntity<JwtAuthenticationResponse> confirmationEmail(@RequestParam("email") String email, @RequestParam("token") String token) {
        return new ResponseEntity<>(authenticationService.confirmationEmail(email, token), HttpStatus.OK);
    }


}
