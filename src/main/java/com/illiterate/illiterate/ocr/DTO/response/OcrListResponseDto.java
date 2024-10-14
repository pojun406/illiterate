package com.illiterate.illiterate.ocr.DTO.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class OcrListResponseDto {
    private Long resultIdx;
    private String title;
    private String infoTitle;
    private String createdAt;
    private String modifyAt;
}
