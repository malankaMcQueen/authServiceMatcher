package com.example.matcher.userservice.service;


import com.example.matcher.userservice.dto.UserDTO;
import com.example.matcher.userservice.exception.EmailNotFoundException;
import com.example.matcher.userservice.exception.ResourceNotFoundException;
import com.example.matcher.userservice.exception.UserAlreadyExistException;
import com.example.matcher.userservice.model.Role;
import com.example.matcher.userservice.model.User;
import com.example.matcher.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    public User registerUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserAlreadyExistException("User with this email already exists");
        }
        User user = new User();
        user.setEmail(userDTO.getEmail());
//        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
        return user;
    }

    public User passwordChange(User user, String newPassword) {
//        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByEmail(username);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Deprecated
    public void getAdmin() {
        var user = getCurrentUser();
        user.setRole(Role.ROLE_ADMIN);
        userRepository.save(user);
    }


    public User getUserById(UUID uuid) {
        return userRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User dont exist " + uuid));
    }
}
