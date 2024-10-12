package com.illiterate.illiterate.ocr.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.common.enums.BoardErrorCode;
import com.illiterate.illiterate.common.enums.MemberErrorCode;
import com.illiterate.illiterate.common.util.LocalFileUtil;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.exception.BoardException;
import com.illiterate.illiterate.member.exception.MemberException;
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
import static com.illiterate.illiterate.common.enums.MemberErrorCode.NOT_FOUND_INFO;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${python.ocr.api.url}")
    private String pythonOcrApiUrl;  // Python 서버의 OCR API URL

    private final OcrRepository ocrRepository;
    private final PaperInfoRepository paperInfoRepository;
    private final LocalFileUtil localFileUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 이미지 업로드 및 OCR 처리 후 결과 저장
     */
    public OcrResponseDto uploadImageAndProcessOcr(MultipartFile file, Member member) {
        // 1. 이미지 업로드 및 경로 가져오기
        String imagePath = localFileUtil.saveImageTmp(file);
        if (imagePath == null) {
            log.error("Image upload failed.");
            throw new RuntimeException("Image upload failed.");
        }
        log.info("Image path: {}", imagePath);

        // 2. Python API 호출하여 OCR 수행
        String ocrResult = callPythonOcrApi(imagePath);
        if (ocrResult == null) {
            log.error("OCR processing failed.");
            throw new RuntimeException("OCR processing failed.");
        }

        // 3. OCR 엔티티 생성 및 저장 (OCR 결과 저장)
        try {

            JsonNode rootNode = objectMapper.readTree(ocrResult);

            // document_index 키가 있는지 확인
            JsonNode documentIndexNode = rootNode.get("document_index");
            if (documentIndexNode == null || documentIndexNode.isNull()) {
                log.error("document_index not found in OCR result.");
                throw new RuntimeException("document_index not found in OCR result.");
            }

            Long documentIdx = documentIndexNode.asLong();
            log.info("문서 번호 : {}", documentIdx);

            // PaperInfo 찾기
            PaperInfo matchedPaperInfo = paperInfoRepository.findByDocumentIndex(documentIdx)
                    .orElseThrow(() -> new MemberException(NOT_FOUND_INFO));

            OCR ocrEntity = saveOcrResult(member, matchedPaperInfo, ocrResult);

            // 4. 임시 파일 삭제
            localFileUtil.deleteImageTmp(imagePath);

            // 5. 결과 반환
            return OcrResponseDto.builder()
                    .ocrText(ocrEntity.getOcrData())
                    .build();

        } catch (MemberException e) {
            log.error("MemberException occurred: {}", e.getMessage());
            throw new MemberException(e.getErrorCode());
        } catch (JsonMappingException e) {
            log.error("Error mapping JSON: {}", e.getMessage());
            throw new RuntimeException("Failed to map JSON.");
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON: {}", e.getMessage());
            throw new RuntimeException("Failed to process JSON.");
        }
    }

    /**
     * Python OCR API를 호출하여 이미지에 대해 OCR 작업을 수행
     *
     * @param imagePath 업로드한 이미지 경로
     * @return OCR 결과 텍스트
     */
    private String callPythonOcrApi(String imagePath) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("image_path", imagePath);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
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

    /**
     * OCR 결과를 JSON 형식으로 저장
     *
     * @param member OCR을 요청한 사용자
     * @param paperInfo OCR과 연관된 PaperInfo 엔티티
     * @param ocrResult OCR 결과 텍스트
     * @return 저장된 OCR 엔티티
     */
    private OCR saveOcrResult(Member member, PaperInfo paperInfo, String ocrResult) {
        OCR ocrEntity = new OCR();
        ocrEntity.setMember(member);
        ocrEntity.setPaperInfo(paperInfo);

        try {
            // OCR 결과를 JSON 형식으로 저장
            Map<String, Object> ocrDataMap = new HashMap<>();
            ocrDataMap.put("ocr_text", ocrResult);
            String ocrDataJson = objectMapper.writeValueAsString(ocrDataMap);
            ocrEntity.setOcrData(ocrDataJson);
        } catch (JsonProcessingException e) {
            log.error("Error converting OCR result to JSON", e);
            throw new RuntimeException("Error saving OCR result.");
        }

        return ocrRepository.save(ocrEntity);
    }
}