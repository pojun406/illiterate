package com.illiterate.illiterate.board.DTO.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BoardRequestDto {
    private String id;
    private String title;
    private String contents;
    private MultipartFile image;
}
