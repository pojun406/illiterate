package com.illiterate.illiterate.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String userid;
    private String password;
    private String username;
    private String phonenum;

    private String role;

    public User() {

    }
}
