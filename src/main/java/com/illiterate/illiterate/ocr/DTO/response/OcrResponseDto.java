package com.illiterate.illiterate.ocr.DTO.response;

import com.illiterate.illiterate.ocr.Entity.OCR;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OcrResponseDto {
    private Long id;
    private String text;
    private List<String> ocrResults;
    private String imageUrl;
}
