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
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolesType roles = RolesType.ROLE_USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusType status = StatusType.ACTIVE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OCR> ocrResults;
}