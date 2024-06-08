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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {

    private static final String UTIL_PYTHON_SCRIPT_PATH = "/pythonProject/letsgopaddle.py";
    private static final String SAVE_TEXT_FOLDER = "/pythonProject/savetext/";

    public OcrResponseDto uploadOCRImage(OcrRequestDto requestDto, MultipartFile image) {
        try {
            // 받은 파일을 임시경로에 새로저장
            String tempDir = System.getProperty("java.io.tmpdir");
            File tempFile = new File(tempDir + "/" + image.getOriginalFilename());
            image.transferTo(tempFile);

            // 파이썬 코드 실행
            List<String> ocrResults = runPythonScript(tempFile);

            // 임시경로에 저장된 이미지 삭제
            Files.delete(Paths.get(tempFile.getAbsolutePath()));

            // 실행 결과 저장
            OcrResponseDto responseDto = new OcrResponseDto();
            responseDto.setOcrResults(ocrResults);
            responseDto.setId(requestDto.getUser().getId());
            return responseDto;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to process OCR", e);
        }
    }

    private List<String> runPythonScript(File file) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", UTIL_PYTHON_SCRIPT_PATH, file.getAbsolutePath());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // 파이썬 스크립트 읽기
        List<String> ocrResults = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ocrResults.add(line);
            }
        }
        process.waitFor();

        // 파이썬 실행 유무 확인
        if (process.exitValue() != 0) {
            throw new IOException("Python script execution failed.");
        }

        return ocrResults;
    }

    public OcrResponseDto saveOcrText(Long ocrId, String text) {
        OcrResponseDto responseDto = new OcrResponseDto();
        responseDto.setId(ocrId);
        responseDto.setText(text);
        return responseDto;
    }
}
