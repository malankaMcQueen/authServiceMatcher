package com.example.matcher.userservice.controllers;


import com.example.matcher.userservice.dto.RecoverPasswordDTO;
import com.example.matcher.userservice.dto.ResetPasswordDTO;
import com.example.matcher.userservice.model.User;
import com.example.matcher.userservice.service.PasswordResetServer;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/password")
@AllArgsConstructor
public class PasswordController {

    private PasswordResetServer passwordResetServer;

    @PostMapping("/recover")
    public ResponseEntity<String> recoverPassword(@RequestBody RecoverPasswordDTO recoverPasswordDTO) {
        return new ResponseEntity<>("Password recovery not implemented", HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody RecoverPasswordDTO recoverPasswordDTO) {
        return new ResponseEntity<>(passwordResetServer.emailConfirmation(recoverPasswordDTO), HttpStatus.OK);
//        return new ResponseEntity<>("Email sent", HttpStatus.OK);
    }

    @GetMapping("/confirmationEmail")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        if (passwordResetServer.isValidToken(token)) {
            return new ResponseEntity<>("Token valid", HttpStatus.OK);
        }
        return new ResponseEntity<>("Token invalid", HttpStatus.UNAUTHORIZED);
    }
//        if (tokenService.isValidToken(token)) {
//            return new ResponseEntity<>("Token is valid", HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Invalid or expired token", HttpStatus.BAD_REQUEST);
//        }
//    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
       return new ResponseEntity<>(passwordResetServer.resetPassword(resetPasswordDTO), HttpStatus.OK);
//        if (!passwordResetServer.isValidToken(token)) {
//            return new ResponseEntity<>("Invalid or expired token", HttpStatus.BAD_REQUEST);
//        }
//
//        User user = tokenService.getUserByToken(token);
//        userService.updatePassword(user, password);
//        tokenService.invalidateToken(token);
//
//        return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
    }
}
