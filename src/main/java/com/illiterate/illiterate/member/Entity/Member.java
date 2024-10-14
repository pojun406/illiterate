package com.illiterate.illiterate.member.Entity;

import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.member.enums.RolesType;
import com.illiterate.illiterate.member.enums.StatusType;
import com.illiterate.illiterate.ocr.Entity.OCR;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_index")
    private Long index;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "roles", nullable = false)
    private RolesType roles = RolesType.ROLE_USER;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusType status = StatusType.ACTIVE;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)  // 'member'로 수정
    private List<Board> boards;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)  // 'user' -> 'member'로 수정
    private List<OCR> ocrResults;
}