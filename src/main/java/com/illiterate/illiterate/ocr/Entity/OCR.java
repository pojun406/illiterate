package com.illiterate.illiterate.ocr.Entity;

import com.illiterate.illiterate.member.Entity.User;
import com.illiterate.illiterate.ocr.DTO.request.OcrRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
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
    @Column(name = "ocr_image")
    private String imagePath;

    @Lob
    @Column(name = "ocr_result")
    private String processedImagePath;

    @Column(name = "result_text")
    private String extractedText;


}
