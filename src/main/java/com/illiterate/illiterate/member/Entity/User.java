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
    private long id;

    private String userid;
    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(255)")
    private RolesType roles = RolesType.ROLE_USER;        // 권한

    public void resetPassword(String password) {
        this.password = password;
    }

}
