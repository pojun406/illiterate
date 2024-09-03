package com.illiterate.illiterate.ocr.Entity;

import com.illiterate.illiterate.member.Entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ocr_result")
public class OCR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ocr_index")
    private Long ocrIndex;

    @ManyToOne
    @JoinColumn(name = "user_index", nullable = false)
    private Member user;

    @Lob
    @Column(name = "image_path")
    private String imagePath;

    @Lob
    @Column(name = "processed_image_path")
    private String processedImagePath;

    @Column(name = "result", columnDefinition = "json", nullable = false)
    private String result;

    @Column(name = "created_at", nullable = false)
    private String createdAt;
}