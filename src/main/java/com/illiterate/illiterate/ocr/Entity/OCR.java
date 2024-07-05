package com.illiterate.illiterate.ocr.Entity;

import com.illiterate.illiterate.member.Entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    private Member user;

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private String createdAt;

    /*@Builder
    private OCR(OcrRequestDto requestsDto, User user, String imagePath) {
        this.title = requestsDto.getTitle();
        this.content = requestsDto.getContents();
        this.user = user;
        this.image = imagePath;
    }*/
}
