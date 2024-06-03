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

    @Column(name = "id")
    private Long uid;

    @Column
    private String writer;

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
    private Board(BoardRequestDto requestsDto) {
        this.title = requestsDto.getTitle();
        this.content = requestsDto.getContents();
    }
    public static Board of(BoardRequestDto requestsDto, String userid) {
        return Board.builder()
                .requestsDto(requestsDto)
                .toString(userid)
                .build();
    }


}
