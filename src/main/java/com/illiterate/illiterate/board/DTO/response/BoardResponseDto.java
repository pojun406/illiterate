package com.illiterate.illiterate.board.DTO.response;

import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.board.enums.StatusType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardResponseDto {
    private Long boardIdx; // Board Index
    private String userId; // 유저 ID
    private String title;
    private String content;
    private String imagePath;
    private StatusType status;
    private String createdAt;
}