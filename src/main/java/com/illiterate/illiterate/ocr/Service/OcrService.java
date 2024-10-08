package com.illiterate.illiterate.ocr.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.common.enums.BoardErrorCode;
import com.illiterate.illiterate.common.util.LocalFileUtil;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.exception.BoardException;
import com.illiterate.illiterate.ocr.DTO.response.OcrResponseDto;
import com.illiterate.illiterate.ocr.Entity.OCR;
import com.illiterate.illiterate.ocr.Entity.PaperInfo;
import com.illiterate.illiterate.ocr.Repository.OcrRepository;
import com.illiterate.illiterate.ocr.Repository.PaperInfoRepository;
import com.illiterate.illiterate.security.Service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.illiterate.illiterate.common.enums.BoardErrorCode.NOT_FOUND_WRITING;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${python.ocr.api.url}")
    private String pythonOcrApiUrl;

    private final OcrRepository ocrRepository;
    private final PaperInfoRepository paperInfoRepository;
    private final LocalFileUtil localFileUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 이미지 업로드 및 OCR 처리 후 결과 저장
    public OcrResponseDto uploadImageAndProcessOcr(MultipartFile file, Member member) {
        // 이미지 저장
        String imagePath = localFileUtil.saveImage(file);
        if (imagePath == null) {
            throw new RuntimeException("Image upload failed.");
        }
        log.info("Image path: {}", imagePath);

        // Python API 호출하여 OCR 수행
        String ocrResult = callPythonOcrApi(imagePath);
        if (ocrResult == null) {
            throw new RuntimeException("OCR processing failed.");
        }

        // 임시 파일 삭제
        localFileUtil.deleteImage(imagePath);

        return OcrResponseDto.builder().ocrText(ocrResult).build();
    }

    private String callPythonOcrApi(String imagePath) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 이미지 경로를 컨테이너에 맞게 변환
            String adjustedImagePath = localFileUtil.adjustImagePath(imagePath);

            Map<String, String> body = new HashMap<>();
            body.put("image_path", adjustedImagePath);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(pythonOcrApiUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                log.error("Python OCR API returned non-successful status code: {}", response.getStatusCodeValue());
                return null;
            }
        } catch (Exception e) {
            log.error("Error calling Python OCR API", e);
            return null;
        }
    }
}