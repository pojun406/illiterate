package com.illiterate.illiterate.ocr.DTO.request;

import com.illiterate.illiterate.member.Entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OcrRequestDto {
    private String title;
    private String ocrData;
}
