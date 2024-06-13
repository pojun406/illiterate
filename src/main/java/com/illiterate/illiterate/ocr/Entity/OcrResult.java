package com.illiterate.illiterate.ocr.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Getter
@Setter
public class OcrResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ElementCollection
    @CollectionTable(name = "ocr_result_map", joinColumns = @JoinColumn(name = "ocr_result_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> result;

    private String modifiedText;
}
