package com.illiterate.illiterate.ocr.DTO.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OcrResponseDto {
    private Long ocrId;
    private String title;
    private String infoTitle;
    private String ocrResult;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
    private String originalImg;
    private String emptyImg;
}
