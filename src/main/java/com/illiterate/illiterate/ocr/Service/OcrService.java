package com.illiterate.illiterate.ocr.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            String tempDir = System.getProperty("java.io.tmpdir");
            File tempFile = new File(tempDir + "/" + image.getOriginalFilename());
            image.transferTo(tempFile);

            // 파이썬 코드 실행
            runPythonScript(tempFile);

            // 임시경로에 저장된 이미지 삭제
            Files.delete(Paths.get(tempFile.getAbsolutePath()));

            // 결과 파일 읽기
            List<String> ocrResults = readOcrResults(new File(tempDir + "/Result.json"));

            // 결과 파일 삭제
            Files.delete(Paths.get(tempDir + "/Result.json"));

            // 실행 결과 저장
            OcrResponseDto responseDto = new OcrResponseDto();
            responseDto.setImageUrl(tempFile.getAbsolutePath());
            responseDto.setId(requestDto.getUser().getId());

            responseDto.setOcrResults(mergeResults(ocrResults));

            return responseDto;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to process OCR", e);
        }
    }

    private void runPythonScript(File file) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", UTIL_PYTHON_SCRIPT_PATH, file.getAbsolutePath());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        process.waitFor();

        if (process.exitValue() != 0) {
            throw new IOException("Python script execution failed.");
        }
    }

    private static List<String> readOcrResults(File resultFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(resultFile);

        List<String> ocrResults = new ArrayList<>();
        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                ocrResults.add(node.get("text").asText());
            }
        }
        return ocrResults;
    }

    private static List<String> mergeResults(List<String> ocrResults) {
        List<String> mergedResults = new ArrayList<>();
        StringBuilder nameBuilder = new StringBuilder();

        boolean isNameSection = false;
        for (String result : ocrResults) {
            if (result.equals("이름")) {
                isNameSection = true;
                mergedResults.add(result);
            } else if (result.equals("주민등록번호")) {
                isNameSection = false;
                if (nameBuilder.length() > 0) {
                    mergedResults.add(nameBuilder.toString());
                    nameBuilder.setLength(0);
                }
                mergedResults.add(result);
            } else if (isNameSection) {
                nameBuilder.append(result);
            } else {
                mergedResults.add(result);
            }
        }

        return mergedResults;
    }

    public OcrResponseDto saveOcrText(Long ocrId, String text) {
        OcrResponseDto responseDto = new OcrResponseDto();
        responseDto.setId(ocrId);
        responseDto.setText(text);
        return responseDto;
    }
}
