package com.illiterate.illiterate.board.DTO.response;

import com.illiterate.illiterate.board.enums.StatusType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardPostResponseDto {
    private Long boardIdx; // Board Index
    private String userId; // 유저 ID
    private String title;
    private StatusType status;
    private String createdAt;
}
