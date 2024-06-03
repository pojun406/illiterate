package com.illiterate.illiterate.ocr.Entity;

import com.illiterate.illiterate.member.Entity.User;
import com.illiterate.illiterate.ocr.DTO.request.OcrRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board")
public class OCR {
    @Id
    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private User user;

    /*@Column
    private String request_img;*/
    @Lob
    @Column(name = "ocr_image", length = 1000)
    private byte[] bimage;

    @Lob
    @Column(name = "ocr_result", length = 1000)
    private byte[] aimage;

    @Column(name = "result_text")
    private String extractedText;

    @Builder
    private OCR(OcrRequestDto requestsDto, User user, byte[] imageData) {
        this.user = user;
        this.image = imageData;
    }

    public static OCR of(OcrRequestDto requestsDto, User user) {
        return OCR.builder()
                .requestsDto(requestsDto)
                .user(user)
                .build();
    }


}
