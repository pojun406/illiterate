package com.illiterate.illiterate.board.Entity;

import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.board.enums.StatusType;
import com.illiterate.illiterate.common.util.LocalFileUtil;
import com.illiterate.illiterate.member.Entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Board")
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
    /*@Column
    private String request_img;*/
    @Lob
    @Column(name = "request_img")
    private String image;

    @Column(name = "reg_date")
    private String regdate;
    @Column(name = "del_date")
    private String deldate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(255)")
    private StatusType status = StatusType.WAIT; // 상태


    @Builder
    private Board(BoardRequestDto requestsDto, User user, String imagePath) {
        this.title = requestsDto.getTitle();
        this.content = requestsDto.getContents();
        this.user = user;
        this.image = imagePath;
    }

    public static Board of(BoardRequestDto requestsDto, User user, String imagePath) {
        return Board.builder()
                .requestsDto(requestsDto)
                .user(user)
                .imagePath(imagePath)
                .build();
    }

    public void updateBoard(BoardRequestDto requestsDto, String imagePath) {
        this.title = requestsDto.getTitle();
        this.content = requestsDto.getContents();
        this.image = imagePath;
    }


}
