package com.illiterate.illiterate.ocr.DTO.response;

import com.illiterate.illiterate.ocr.Entity.OCR;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OcrResponseDto {
    private byte[] before_img;
    private byte[] after_img;

    @Builder
    private OcrResponseDto(OCR ocr){
        this.before_img = ocr.getBimage();
        this.after_img = ocr.getAimage();
    }

    /*public static OcrResponseDto from(OCR ocr) {
        return OcrResponseDto.builder()
                .board(board)
                .build();
    }*/
}
