package com.food.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "users")
public class User {
    public User(String contact, String email, String password, String role) {
        this.contact = contact;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Id
    private String id;
    private String contact;
    private String email;
    private String password;
    private String role;

}

