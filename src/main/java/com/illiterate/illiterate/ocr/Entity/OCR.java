package com.illiterate.illiterate.ocr.Entity;

import com.illiterate.illiterate.member.Entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.Date;

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
    @JoinColumn(name = "document_index", nullable = false)
    private PaperInfo paperInfo; // PaperInfo와 N:1 관계

    @ManyToOne
    @JoinColumn(name = "user_index", nullable = false)
    private Member member; // user와 N:1 관계

    @Column(name = "ocr_data", columnDefinition = "JSON", nullable = false)
    private String ocrData; // OCR 결과 JSON으로 저장

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
}