package com.illiterate.illiterate.admin.DTO.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminResponseDto {
    private String titleVector;
    private String titleText;
    private String ocrResult;
    private String titleImg;

}