package com.example.userauthservice.pojos;

import com.example.userauthservice.models.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponse {
    private User user;
    private String token;
}
