package com.illiterate.illiterate.board.Entity;

import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.board.enums.StatusType;
import com.illiterate.illiterate.member.Entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_index")
    private Long boardId;

    @ManyToOne
    @JoinColumn(name = "user_index", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Lob
    @Column(name = "request_img")
    private String requestImg;

    @Column(name = "reg_date", nullable = false)
    private String regDate;

    @Column(name = "del_date")
    private String delDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusType status = StatusType.WAIT;
}