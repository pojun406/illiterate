package com.illiterate.illiterate.ocr.DTO.response;

import com.illiterate.illiterate.ocr.Entity.OCR;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OcrResponseDto {
    private String text;
    private String imageUrl;

}
