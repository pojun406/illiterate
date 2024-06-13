package com.illiterate.illiterate.ocr.Repository;

import com.illiterate.illiterate.ocr.Entity.OCR;
import com.illiterate.illiterate.ocr.Entity.OcrResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcrResultRepository extends JpaRepository<OcrResult, Long> {

}
