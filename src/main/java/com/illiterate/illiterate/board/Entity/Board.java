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
    @Column(name = "board_id")
    private Long bid;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private User user;

    @Column
    private String title;
    @Column
    private String content;
    @Column
    private String request_img;
    @Column
    private String reg_date;
    @Column
    private String del_date;
    @Column
    private String status;

    @Builder
    private Board(BoardRequestDto requestsDto, User user) {
        this.title = requestsDto.getTitle();
        this.content = requestsDto.getContents();
        this.user = user;
    }
    public static Board of(BoardRequestDto requestsDto, User user) {
        return Board.builder()
                .requestsDto(requestsDto)
                .user(user)
                .build();
    }


}
