package com.illiterate.illiterate.ocr.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name = "paper_info")
public class PaperInfo {

    @Id
    private String title;

    private String titleVector;

    @Column(columnDefinition = "json")
    private String vectorAndValue;

    @Column(nullable = false)
    private Timestamp createdAt;

    // Getters and setters
}

