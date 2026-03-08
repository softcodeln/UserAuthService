package com.example.userauthservice.configurations;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;

@Configuration
public class AuthConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        auth -> auth.anyRequest()
                        .permitAll());

        return httpSecurity.build();
    }

    @Bean
    public SecretKey getSecretKey() {
        // In a real application, you should store this key securely and not hard-code it
        // 1. Jwt Header part contains the algorithm used for signing the token
        MacAlgorithm algorithm = Jwts.SIG.HS256;
        // 2. Secret key for signing the token generated using the algorithm;
        return algorithm.key().build();
    }
}
