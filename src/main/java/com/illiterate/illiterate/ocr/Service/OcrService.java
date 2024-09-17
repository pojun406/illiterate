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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static com.illiterate.illiterate.common.enums.BoardErrorCode.NOT_FOUND_WRITING;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {

    @Value("${python.ocr.script.path}")
    private String pythonScriptPath;

    @Value("${python.executable.path}")
    private String pythonExecutable;

    private final OcrRepository ocrRepository;
    private final PaperInfoRepository paperInfoRepository;  // PaperInfo와 상호작용하기 위한 리포지토리
    private final LocalFileUtil localFileUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();  // JSON 처리용 ObjectMapper

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

        // 2. 업로드된 이미지가 어떤 문서인지 판별
        PaperInfo matchedPaperInfo = findMatchingPaperInfo(imagePath);
        if (matchedPaperInfo == null) {
            log.error("Document type could not be determined.");
            throw new RuntimeException("Document type could not be determined.");
        }

        // 3. Python 스크립트를 실행하여 OCR 수행
        String ocrResult = executePythonOcrScript(imagePath);
        if (ocrResult == null) {
            log.error("OCR processing failed.");
            throw new RuntimeException("OCR processing failed.");
        }

        // 4. OCR 엔티티 생성 및 저장 (OCR 결과 저장)
        OCR ocrEntity = saveOcrResult(member, matchedPaperInfo, ocrResult);

        // 5. 결과 반환
        return OcrResponseDto.builder()
                .ocrText(ocrEntity.getOcrData())  // OCR 데이터를 반환
                .build();
    }

    /**
     * 업로드된 이미지와 PaperInfo의 타이틀 이미지/벡터값을 비교하여 문서 유형을 판별
     *
     * @param imagePath 업로드된 이미지 경로
     * @return 매칭된 PaperInfo 엔티티
     */
    private PaperInfo findMatchingPaperInfo(String imagePath) {
        List<PaperInfo> paperInfoList = paperInfoRepository.findAll();  // 모든 PaperInfo 데이터를 가져옴

        for (PaperInfo paperInfo : paperInfoList) {
            // 1. 타이틀 이미지와 업로드된 이미지 비교 (이미지 판별 로직 필요)
            boolean isTitleImageMatch = compareImages(paperInfo.getTitleVector(), imagePath);

            // 2. 벡터 값과도 비교 가능
            if (isTitleImageMatch) {
                return paperInfo;  // 문서가 매칭되면 해당 PaperInfo 반환
            }
        }

        return null;  // 매칭되는 문서가 없을 경우 null 반환
    }

    /**
     * 이미지 비교 로직 (타이틀 이미지와 업로드된 이미지 비교)
     * python코드를 사용해서 이미지 분석을 시행할 예정
     */
    private boolean compareImages(String titleVector, String imagePath) {
        // TODO: 타이틀 이미지 벡터와 업로드된 이미지의 벡터 비교 로직 구현
        // 예시: 이미지 벡터 유사도를 측정하여 true/false 반환
        return true;  // 간단히 true로 처리 (실제 구현 필요)
    }

    /**
     * Python 스크립트를 실행하여 OCR 작업을 수행
     * Python 코드 자체는 아직 준비가 되지 않았음 추후 로직수정
     *
     * @param imagePath 업로드한 이미지 경로
     * @return OCR 결과 텍스트
     */
    private String executePythonOcrScript(String imagePath) {
        CommandLine commandLine = new CommandLine(pythonExecutable);
        commandLine.addArgument(pythonScriptPath);
        commandLine.addArgument(imagePath);  // 이미지 경로를 인자로 추가

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);

        try {
            executor.execute(commandLine);  // Python 스크립트 실행
            return outputStream.toString();  // OCR 결과 반환
        } catch (IOException e) {
            log.error("Error executing Python OCR script.", e);
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