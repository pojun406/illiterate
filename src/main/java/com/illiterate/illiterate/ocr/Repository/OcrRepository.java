package com.illiterate.illiterate.ocr.Repository;

import com.illiterate.illiterate.member.Entity.User;
import com.illiterate.illiterate.ocr.Entity.OCR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OcrRepository extends JpaRepository<OCR, Long> {

    List<OCR> findAll();
    Optional<OCR> findByImage(String fileName);

    Optional<OCR> findByBidAndUser(Long bid, User user);
}
