package com.example.userauthservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class UserDto {
    private UUID id;
    private String name;
    private String email;
}
