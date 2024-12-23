package com.example.matcher.userservice.controllers;


import com.example.matcher.userservice.model.User;
import com.example.matcher.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Hidden
@RestController
@AllArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    @GetMapping("/getInfo")
    public ResponseEntity<User> getById() {
        // Извлечь UUID из контекста безопасности
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal(); // UUID будет в качестве principal

        User user = userService.getUserById(UUID.fromString(userId)); // Вызов сервиса для получения пользователя
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
