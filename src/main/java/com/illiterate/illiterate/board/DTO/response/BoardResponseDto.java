package com.illiterate.illiterate.board.DTO.response;

import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.board.enums.StatusType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardResponseDto {
    private Long id; // user_index
    private String title;
    private String content;
    private String imagePath;
    private StatusType status;
}