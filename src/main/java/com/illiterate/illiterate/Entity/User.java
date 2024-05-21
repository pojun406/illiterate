package com.illiterate.illiterate.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user")
public class User {
    @Id
    private int uuid;
    private String uid;
    private String username;
    private String password;
    private String phonenum;

    private User(String uid, String password) {
        this.uid = uid;
        this.password = password;
    }

    public User() {

    }
}
