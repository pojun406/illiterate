package com.illiterate.illiterate.ocr.DTO.response;

import com.illiterate.illiterate.ocr.Entity.OCR;
import jdk.jshell.Snippet;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class OcrResponseDto {
    private String ocrText;
}
