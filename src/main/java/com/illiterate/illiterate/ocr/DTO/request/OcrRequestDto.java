package com.illiterate.illiterate.ocr.DTO.request;

import com.illiterate.illiterate.member.Entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OcrRequestDto {
    private User user;
    private String imagePath;

}
