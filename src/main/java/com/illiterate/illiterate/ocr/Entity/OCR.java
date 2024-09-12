package com.illiterate.illiterate.ocr.Entity;

import com.illiterate.illiterate.member.Entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ocr_results")
public class OCR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "title")
    private PaperInfo title;

    @Column(columnDefinition = "json")
    private String fieldValue;

    @Column(nullable = false)
    private Timestamp createdAt;
}