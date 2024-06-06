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
    private static final String IMAGE_SAVE_PATH = "/pythonProject/saveimage/";
    private final OcrRepository ocrRepository;

    public OcrResponseDto uploadOCRImage(OcrRequestDto request) {

        OcrResponseDto response = new OcrResponseDto();

        try {
            String imagePath = request.getImagePath();
            String processedImagePath = IMAGE_SAVE_PATH + "/processed_" + System.currentTimeMillis() + ".png";

            ProcessBuilder processBuilder = new ProcessBuilder("python3", UTIL_PYTHON_SCRIPT_PATH, imagePath, processedImagePath);
            Process process = processBuilder.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String resultText = in.readLine();
            in.close();

            OCR ocrResult = new OCR();
            ocrResult.setImagePath(imagePath);
            ocrResult.setExtractedText(resultText);
            ocrResult.setProcessedImagePath(processedImagePath);
            ocrRepository.save(ocrResult);

            response.setText(resultText);
            response.setImageUrl(processedImagePath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}
