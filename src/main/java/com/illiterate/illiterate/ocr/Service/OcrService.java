package com.illiterate.illiterate.ocr.Service;

import com.illiterate.illiterate.common.util.ConvertUtil;
import com.illiterate.illiterate.common.util.LocalFileUtil;
import com.illiterate.illiterate.member.exception.MemberException;
import com.illiterate.illiterate.ocr.DTO.request.OcrRequestDto;
import com.illiterate.illiterate.ocr.DTO.response.OcrResponseDto;
import com.illiterate.illiterate.ocr.Entity.OCR;
import com.illiterate.illiterate.ocr.Repository.OcrRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {
    private static final String UTIL_PYTHON_SCRIPT_PATH = "/pythonProject/run.py";
    private static final String IMAGE_SAVE_PATH = "/pythonProject/savetext/";
    private final OcrRepository ocrRepository;

    public OcrResponseDto uploadOCRImage(OcrRequestDto request, MultipartFile image) {
        OcrResponseDto response = new OcrResponseDto();

        try {
            // 이미지 저장 로직
            String imagePath = LocalFileUtil.saveImage(image, IMAGE_SAVE_PATH);

            // OCR 처리
            String processedImagePath = IMAGE_SAVE_PATH + "/processed_" + System.currentTimeMillis() + ".png";
            ProcessBuilder processBuilder = new ProcessBuilder("python3", UTIL_PYTHON_SCRIPT_PATH, imagePath, processedImagePath);
            Process process = processBuilder.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String resultText = in.readLine();
            in.close();

            // DB에 저장
            OCR ocrResult = OCR.builder()
                    .user(request.getUser())
                    .imagePath(imagePath)
                    .processedImagePath(processedImagePath)
                    .extractedText(resultText)
                    .isProcessed(false)
                    .build();
            ocrRepository.save(ocrResult);

            response.setId(ocrResult.getId());
            response.setText(resultText);
            response.setImageUrl(processedImagePath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public OcrResponseDto saveOcrText(Long ocrId, String text) {
        OCR ocrResult = ocrRepository.findById(ocrId).orElseThrow(() -> new RuntimeException("OCR 결과물이 없습니다."));

        ocrResult.setExtractedText(text);
        ocrResult.setProcessed(true);
        ocrRepository.save(ocrResult);

        OcrResponseDto response = new OcrResponseDto();
        response.setId(ocrResult.getId());
        response.setText(ocrResult.getExtractedText());
        response.setImageUrl(ocrResult.getProcessedImagePath());

        return response;
    }
}
