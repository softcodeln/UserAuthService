package com.example.userauthservice.controllers;

import com.example.userauthservice.dtos.LoginRequestDto;
import com.example.userauthservice.dtos.SignUpRequestDto;
import com.example.userauthservice.dtos.UserDto;
import com.example.userauthservice.dtos.ValidateToken;
import com.example.userauthservice.models.User;
import com.example.userauthservice.pojos.LoginResponse;
import com.example.userauthservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    /*
    Endpoint for user registration/signup and login will be implemented here. This will include:
    sign-up and login functionality.

    1. sign-up endpoint:
    POST api/v1/auth/signup
    Description : { This endpoint allows new users to register by providing their name, email, and password.
    The system will validate the input, create a new user account,
    and return the user's details (excluding the password) upon successful registration. }
    content-type: application/json
    Request body = SignUpRequestDto
    Response body = UserDto
    Status codes:
    201 Created: User successfully registered.
    400 Bad Request: Invalid input data (e.g., missing fields, invalid email format).
    Example Request and Response:
    Request body = {
        "name": "test user",
        "email": "testmail@gmail.com"
        "password": "testpassword"
        }
        Response: ResponseEntity having payload and header
    Response body = { // payload
        "id": 1,
        "name": "test user",
        "email": "testmail@gmail.com",
        }
        Header have JWT.

      2. login endpoint:
    POST api/v1/auth/login
    Description : { This endpoint allows users to login by providing their email, and password.
    The system will validate the input, then let them log in to app,
    and return the user's details (excluding the password) upon successful login. }
    content-type: application/json
    Request body = LoginRequestDto
    Response body = UserDto
    Status codes:
    201 Created: User successfully logged in.
    Example Request and Response:
    Request body = {
        "email": "testmail@gmail.com"
        "password": "testpassword"
        }
        Response: ResponseEntity having payload and header
    Response body = { // payload
        "id": uuid,
        "name": "test user",
        "email": "testmail@gmail.com",
        }
        Header have JWT.
    */
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        User user = authService.signUp(signUpRequestDto.getName(), signUpRequestDto.getEmail(), signUpRequestDto.getPassword());
        return new ResponseEntity<>(user.getUserDto(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponse loginResponse = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", loginResponse.getToken());
        return new ResponseEntity<>(loginResponse.getUser().getUserDto(), headers,HttpStatus.CREATED);
    }
    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestBody ValidateToken tokenDto) {
        boolean isValid = authService.validateToken(tokenDto.getToken());
        if (isValid) {
            return new ResponseEntity<>("Token is valid", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Token is invalid", HttpStatus.UNAUTHORIZED);
        }
    }
}
