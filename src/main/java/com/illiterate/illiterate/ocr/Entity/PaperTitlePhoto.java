package com.illiterate.illiterate.ocr.Entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "paper_title_photo")
public class PaperTitlePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "title")
    private PaperInfo title;

    private String croppedImage;

    private String matchedVector;

    @Column(nullable = false)
    private Timestamp createdAt;

    // Getters and setters
}
