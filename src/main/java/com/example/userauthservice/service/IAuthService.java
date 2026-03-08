package com.example.userauthservice.service;

import com.example.userauthservice.models.User;
import com.example.userauthservice.pojos.LoginResponse;

public interface IAuthService {
    public User signUp(String name, String email, String password);
    public LoginResponse login(String email, String password);
    public boolean validateToken(String token);
}
