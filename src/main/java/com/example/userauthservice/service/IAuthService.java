package com.example.userauthservice.service;

import com.example.userauthservice.models.User;

public interface IAuthService {
    public User signUp(String name, String email, String password);
    public User login(String email, String password);
}
