package com.example.userauthservice.dtos;

import lombok.Data;

@Data
public class SignUpRequestDto {
    private String name;
    private String email;
    private String password;
}
