package com.example.userauthservice.models;

import com.example.userauthservice.dtos.UserDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel{
    private String name;
    private String email;
    private String password;
    @ManyToMany
    private List<Role> roles;

    public UserDto getUserDto() {
        return UserDto
                .builder()
                .name(this.name)
                .email(this.email)
                .id(this.getPublicId())
                .build();
        // 🙂 we have used Builder design pattern to create UserDto object, we can also use constructor to create UserDto object.
    }
}
