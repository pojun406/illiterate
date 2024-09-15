package com.illiterate.illiterate.ocr.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "paper_info")
public class PaperInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_index")
    private Long documentIndex;

    @Column(name = "title_vector", nullable = false)
    private String titleVector;

    @Column(name = "title_text",nullable = false)
    private String titleText;

    @Column(name = "img_info", columnDefinition = "JSON")
    private String imgInfo;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
}
