package com.illiterate.illiterate.board.DTO.response;

import com.illiterate.illiterate.board.Entity.Board;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardResponseDto {
    private long board_id;
    private String title;
    private String username;
    private String content;
    private byte[] request_img;
    private String reg_date;
    private String del_date;
    private String status;

    @Builder
    private BoardResponseDto(Board board){
        this.board_id = board.getBid();
        this.title = board.getTitle();
        this.username = board.getUser().getUsername();
        this.content = board.getContent();
        this.request_img = board.getImage();
        this.reg_date = board.getRegdate();
        this.del_date = board.getDeldate();
        this.status = board.getStatus();
    }

    public static BoardResponseDto from(Board board) {
        return BoardResponseDto.builder()
                .board(board)
                .build();
    }
}
