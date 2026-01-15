package com.kote.banking.dto;

import com.kote.banking.entity.enums.UserRole;
import lombok.Data;

@Data
public class AuthenticationRequestDto {
    private String name;
    private String email;
    private String password;
    private UserRole role;
}