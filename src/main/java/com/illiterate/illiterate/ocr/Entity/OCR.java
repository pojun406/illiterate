package com.illiterate.illiterate.ocr.Entity;

import com.illiterate.illiterate.member.Entity.User;
import com.illiterate.illiterate.ocr.DTO.request.OcrRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ocr_result")
public class OCR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /*@Column
    private String request_img;*/
    @Lob
    @Column(name = "image_path")
    private String imagePath;

    @Lob
    @Column(name = "processed_image_path")
    private String processedImagePath;

    @Column(name = "result", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String result;
}
