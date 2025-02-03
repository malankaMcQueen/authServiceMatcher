package com.example.matcher.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    String email;
    Long telegramId;
}
