package com.illiterate.illiterate.member.Entity;

import com.illiterate.illiterate.member.enums.RolesType;
import com.illiterate.illiterate.member.enums.StatusType;
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
@Table(name = "User")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userid;
    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(255)")
    private RolesType roles = RolesType.USER;        // 권한

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(255)")
    private StatusType status = StatusType.ACTIVE; // 상태


    public void resetPassword(String password) {
        this.password = password;
    }

    public void updateName(String name) {
        this.username = name;
    }

    public void updateEmail(String email){ this.email = email; }

    public void inactivateUser(){ this.status = StatusType.INACTIVE; }

    public RolesType getRole() { return roles; }
}
