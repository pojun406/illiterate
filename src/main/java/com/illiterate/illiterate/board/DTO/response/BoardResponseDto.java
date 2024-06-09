package com.illiterate.illiterate.board.DTO.response;

import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.board.enums.StatusType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private String imagePath;
    private StatusType status;

    public static BoardResponseDto from(Board board) {
        return BoardResponseDto.builder()
                .id(board.getBid())
                .title(board.getTitle())
                .content(board.getContent())
                .imagePath(board.getImage())
                .status(board.getStatus())
                .build();
    }
}