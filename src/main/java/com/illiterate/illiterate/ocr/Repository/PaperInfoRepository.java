package com.illiterate.illiterate.ocr.Repository;

import com.illiterate.illiterate.ocr.Entity.PaperInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaperInfoRepository extends JpaRepository<PaperInfo, Long> {
    Optional<PaperInfo> findByDocumentIndex(Long documentIdx);
}
