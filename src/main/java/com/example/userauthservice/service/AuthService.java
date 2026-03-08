package com.example.userauthservice.service;

import com.example.userauthservice.exceptions.IncorrectPasswordException;
import com.example.userauthservice.exceptions.UserAlreadyExistException;
import com.example.userauthservice.exceptions.UserNotRegisteredException;
import com.example.userauthservice.models.Role;
import com.example.userauthservice.models.Session;
import com.example.userauthservice.models.State;
import com.example.userauthservice.models.User;
import com.example.userauthservice.pojos.LoginResponse;
import com.example.userauthservice.repositories.RoleRepository;
import com.example.userauthservice.repositories.SessionRepository;
import com.example.userauthservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecretKey secretKey;
    private final SessionRepository sessionRepository;

    @Override
    public User signUp(String name, String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            // throw user already exists exception
            throw new UserAlreadyExistException("User with email " + email + " already exists");
        }

        Role role;
        if(roleRepository.findByName("DEFAULT").isEmpty()){
            role = roleRepository.save(
                    Role
                    .builder()
                    .name("DEFAULT")
                    .createdAt(new Date())
                    .lastUpdatedAt(new Date())
                    .state(State.ACTIVE)
                    .build()
            );
        } else {
            role = roleRepository.findByName("DEFAULT").get();
        }
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        return userRepository.save(
                User
                        .builder()
                        .name(name)
                        .email(email)
                        .password(bCryptPasswordEncoder.encode(password))
                        .roles(roles)
                        .createdAt(new Date())
                        .lastUpdatedAt(new Date())
                        .state(State.ACTIVE)
                        .build()
        );
    }

    @Override
    public LoginResponse login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            //throw user not found exception
            throw new UserNotRegisteredException("User with email " + email + " not found");
        } else if (!bCryptPasswordEncoder.matches(password, user.get().getPassword())) {
            //throw invalid credentials exception
            throw new IncorrectPasswordException("Incorrect password");
        }
        // Payload for JWT token generation
        Map<String, Object> payload = new HashMap<>();
        payload.put("iss", "scaler");
        payload.put("aud","users");
        payload.put("iat", System.currentTimeMillis());
        payload.put("sub", String.valueOf(user.get().getPublicId()));
        payload.put("exp" , System.currentTimeMillis() + 3600000); // 1 hour expiry
        payload.put("scope", user.get().getRoles());

        // Signature part of the token is generated using the header, payload and the secret key
        String jwtToken = Jwts.builder().claims(payload).signWith(secretKey).compact();

        sessionRepository.save(
                Session
                        .builder()
                        .token(jwtToken)
                        .user(user.get())
                        .createdAt(new Date())
                        .lastUpdatedAt(new Date())
                        .state(State.ACTIVE)
                        .build()
        );

        return LoginResponse
                .builder()
                .token(jwtToken)
                .user(user.get())
                .build();
    }

    public boolean validateToken(String token) {
        try {
            // Check if token exists in session table
            Session session = sessionRepository.findByToken(token).orElse(null);
            if (session == null) {
                return false;
            }
            // Verify JWT signature and expiration
            JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            long expiryTime = ((Number) claims.get("exp")).longValue();
            long nowInMills = System.currentTimeMillis();

            if (nowInMills > expiryTime) {
                session.setState(State.INACTIVE);
                sessionRepository.save(session);
                return false;
            }
            return true;
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

}
