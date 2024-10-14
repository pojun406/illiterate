package com.illiterate.illiterate.ocr.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Column(name = "title_img",nullable = false)
    private String titleImg;

    @Column(name = "title_text", nullable = false)
    private String titleText;

    @Column(name = "img_info", columnDefinition = "JSON")
    private String imgInfo;

    @Column(name = "empty_img")
    private String emptyImg;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "modify_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifyAt;

    @PrePersist
    protected void onCreate(){
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        modifyAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        modifyAt = LocalDateTime.now();
    }
}
