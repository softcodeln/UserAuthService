package com.example.userauthservice.dtos;

import com.example.userauthservice.models.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private List<Role> roles;
}
