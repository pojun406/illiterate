package com.illiterate.illiterate.ocr.Service;

import com.illiterate.illiterate.ocr.Repository.OcrRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {
    private final OcrRepository ocrRepository;


}
