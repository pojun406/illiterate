package com.illiterate.illiterate.ocr.Entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "user_image_process")
public class UserImageProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userId;

    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "detected_title")
    private PaperInfo detectedTitle;

    @Column(nullable = false)
    private Timestamp processDate;

    // Getters and setters
}
