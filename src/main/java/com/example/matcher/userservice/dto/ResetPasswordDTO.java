package com.example.matcher.userservice.dto;

import lombok.Data;

@Data
public class ResetPasswordDTO {
    String token;
    String password;
}
