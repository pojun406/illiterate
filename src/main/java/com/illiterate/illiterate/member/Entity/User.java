package com.illiterate.illiterate.member.Entity;

import com.illiterate.illiterate.member.enums.RolesType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String userid;
    private String username;
    private String password;
    private String email;

    private RolesType roles = RolesType.ROLE_USER;        // 권한

}