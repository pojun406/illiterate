package com.illiterate.illiterate.board.Entity;

import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.member.Entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long board_id;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private User user;
    private String writer;
    private String title;
    private String content;
    private String request_img;
    private String reg_date;
    private String del_date;
    private String status;

    @Builder
    private Board(BoardRequestDto requestsDto, User user) {
        this.title = requestsDto.getTitle();
        this.content = requestsDto.getContents();
        this.user = user;
    }

}
