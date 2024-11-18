package com.illiterate.illiterate.admin.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.illiterate.illiterate.admin.DTO.request.AdminRequestDto;
import com.illiterate.illiterate.admin.DTO.request.PaperInfoRequestDto;
import com.illiterate.illiterate.admin.DTO.response.AdminResponseDto;
import com.illiterate.illiterate.common.util.LocalFileUtil;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.ocr.DTO.response.OcrResponseDto;
import com.illiterate.illiterate.ocr.Entity.PaperInfo;
import com.illiterate.illiterate.ocr.Repository.PaperInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final PaperInfoRepository paperInfoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final LocalFileUtil localFileUtil;

    @Value("${python.paperinfo.api.url}")
    private String pythonPaperInfoApiUrl;

    /**
     * /paperinfo 엔드포인트 호출 메서드
     */
    private String callPythonPaperInfoApi(String imagePath) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("image_path", imagePath);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

            ResponseEntity<String> response = restTemplate.postForEntity(pythonPaperInfoApiUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                log.error("Python PaperInfo API returned non-successful status code: {}", response.getStatusCodeValue());
                return null;
            }
        } catch (Exception e) {
            log.error("Error calling Python PaperInfo API", e);
            return null;
        }
    }

    /**
     * /paperinfo 호출 및 결과 처리 메서드
     */
    public AdminResponseDto uploadImageAndProcessPaperInfo(String path, PaperInfoRequestDto requestDto) {
        String paperInfoResult = callPythonPaperInfoApi(path);
        if (paperInfoResult == null) {
            log.error("PaperInfo processing failed.");
            throw new RuntimeException("PaperInfo processing failed.");
        }

        try {
            // JSON 응답을 JsonNode로 파싱
            JsonNode rootNode = objectMapper.readTree(paperInfoResult);

            // title_img 필드 처리
            String titleImageBase64 = rootNode.path("title_img").asText();
            if (titleImageBase64.isEmpty()) {
                log.error("title_img not found in PaperInfo result.");
                throw new RuntimeException("title_img not found in PaperInfo result.");
            }

            // Base64 이미지를 파일로 저장
            String savedImagePath = localFileUtil.saveImageFromBase64(requestDto.getInfoTitle(), titleImageBase64, "paperinfo", "png");
            if (savedImagePath == null) {
                log.error("Failed to save title image from Base64.");
                throw new RuntimeException("Failed to save title image from Base64.");
            }

            // PaperInfo 엔티티에 저장
            PaperInfo paperInfo = new PaperInfo();

// title 또는 제목의 벡터 값 설정
            JsonNode roiDataNode = rootNode.path("roi_data");
            String titleVector = null;

            Iterator<Map.Entry<String, JsonNode>> fields = roiDataNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String description = entry.getValue().asText();

                // "title"이나 "제목"이라는 설명이 있으면 해당 벡터 값으로 설정
                if ("title".equals(description) || "제목".equals(description)) {
                    titleVector = entry.getKey();
                    break;
                }
            }

            if (titleVector != null) {
                paperInfo.setTitleVector(titleVector);
            } else {
                log.error("Neither 'title' nor '제목' found in ROI data.");
                throw new RuntimeException("Neither 'title' nor '제목' found in ROI data.");
            }

            // 나머지 ROI 데이터를 저장
            paperInfo.setImgInfo(objectMapper.writeValueAsString(rootNode.path("roi_data")));
            paperInfo.setTitleImg(savedImagePath); // 저장된 이미지 경로 설정
            //TODO : 이거 프론트에서 실행시킬때 입력받게 만들어야함.
            paperInfo.setTitleText("테스트");
            paperInfo.setEmptyImg("빈이미지 테스트값");
            paperInfoRepository.save(paperInfo);

            return AdminResponseDto.builder()
                    .titleVector(paperInfo.getTitleVector())
                    .titleText(paperInfo.getTitleText())
                    .ocrResult(paperInfo.getImgInfo())
                    .titleImg(savedImagePath) // 응답에 포함 가능
                    .build();

        } catch (Exception e) {
            log.error("Error processing PaperInfo JSON", e);
            throw new RuntimeException("Failed to process PaperInfo JSON.");
        }
    }
}
