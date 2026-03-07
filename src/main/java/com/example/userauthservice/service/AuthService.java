package com.example.userauthservice.service;

import com.example.userauthservice.exceptions.IncorrectPasswordException;
import com.example.userauthservice.exceptions.UserAlreadyExistException;
import com.example.userauthservice.exceptions.UserNotRegisteredException;
import com.example.userauthservice.models.Role;
import com.example.userauthservice.models.State;
import com.example.userauthservice.models.User;
import com.example.userauthservice.repositories.RoleRepository;
import com.example.userauthservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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
                        .password(password)
                        .roles(roles)
                        .createdAt(new Date())
                        .lastUpdatedAt(new Date())
                        .state(State.ACTIVE)
                        .build()
        );
    }

    @Override
    public User login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            //throw user not found exception
            throw new UserNotRegisteredException("User with email " + email + " not found");
        }

        if(!user.get().getPassword().equals(password)){
            //throw invalid credentials exception
            throw new IncorrectPasswordException("Incorrect password");
        }

        return user.get();
    }
}
