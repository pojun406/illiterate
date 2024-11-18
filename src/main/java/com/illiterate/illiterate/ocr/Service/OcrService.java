package com.illiterate.illiterate.ocr.Service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.illiterate.illiterate.common.util.LocalFileUtil;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.exception.MemberException;
import com.illiterate.illiterate.member.Repository.MemberRepository;
import com.illiterate.illiterate.ocr.DTO.request.OcrFileNameRequest;
import com.illiterate.illiterate.ocr.DTO.request.OcrRequestDto;
import com.illiterate.illiterate.ocr.DTO.response.OcrListResponseDto;
import com.illiterate.illiterate.ocr.DTO.response.OcrResponseDto;
import com.illiterate.illiterate.ocr.Entity.OCR;
import com.illiterate.illiterate.ocr.Entity.PaperInfo;
import com.illiterate.illiterate.ocr.Repository.OcrRepository;
import com.illiterate.illiterate.ocr.Repository.PaperInfoRepository;
import com.illiterate.illiterate.security.Service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.illiterate.illiterate.common.enums.MemberErrorCode.NOT_FOUND_INFO;
import static com.illiterate.illiterate.common.enums.MemberErrorCode.NOT_FOUND_MEMBER_ID;

@Slf4j
@Service
public class OcrService {

    @Value("${python.ocr.api.url}")
    private String pythonOcrApiUrl;

    private ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final OcrRepository ocrRepository;
    private final PaperInfoRepository paperInfoRepository;
    private final LocalFileUtil localFileUtil;
    private final MemberRepository memberRepository;

    public OcrService(
            RestTemplate restTemplate,
            OcrRepository ocrRepository,
            PaperInfoRepository paperInfoRepository,
            LocalFileUtil localFileUtil,
            MemberRepository memberRepository
    ) {
        this.restTemplate = restTemplate;
        this.ocrRepository = ocrRepository;
        this.paperInfoRepository = paperInfoRepository;
        this.localFileUtil = localFileUtil;
        this.memberRepository = memberRepository;

        // ObjectMapper의 비ASCII 설정
        this.objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
    }

    /**
     * 이미지 업로드 및 OCR 처리 후 결과 저장
     */
    public OcrResponseDto uploadImageAndProcessOcr(MultipartFile file, Member member) {
        // 1. 이미지 업로드 및 경로 가져오기
        String imagePath = localFileUtil.saveImage(file, "ocr");
        System.out.println("이미지경로 : " + imagePath);
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
            // JSON 문자열의 이스케이프 문자와 필요없는 문자를 정리
            String cleanJsonString = ocrResult
                    .replaceAll("\\\\\"", "\"")
                    .replaceAll("\\\\n", "")
                    .replaceAll("\\\\t", "")
                    .replaceAll("^\"|\"$", ""); // 문자열 양쪽에 있는 불필요한 큰따옴표 제거
            log.info("정리된 OCR JSON 응답: {}", cleanJsonString);

            // JSON 응답을 JsonNode로 파싱
            JsonNode rootNode = objectMapper.readTree(cleanJsonString);
            log.info("루트 노드 : {}", rootNode);

            // document_index 키가 있는지 확인
            JsonNode documentIndexNode = rootNode.get("document_index");
            log.info("문서 번호 json에서 바로 뽑은거 : {}", documentIndexNode);
            if (documentIndexNode == null || documentIndexNode.isNull()) {
                log.error("document_index not found in OCR result.");
                throw new RuntimeException("document_index not found in OCR result.");
            }

            // document_index 값 추출
            Long documentIdx = documentIndexNode.asLong();
            log.info("문서 번호 : {}", documentIdx);

            // PaperInfo 찾기
            PaperInfo matchedPaperInfo = paperInfoRepository.findByDocumentIndex(documentIdx)
                    .orElseThrow(() -> new MemberException(NOT_FOUND_INFO));
            OCR ocr = saveOcrResult(imagePath, member, matchedPaperInfo, cleanJsonString);

            return OcrResponseDto.builder()
                    .ocrResult(ocr.getOcrData())
                    .ocrId(ocr.getOcrIndex())
                    .originalImg(ocr.getImage())
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

    private String callPythonOcrApi(String adjustedImagePath) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("image_path", adjustedImagePath);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

            // UTF-8 인코딩을 위한 메시지 컨버터 설정
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

            ResponseEntity<String> response = restTemplate.postForEntity(pythonOcrApiUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return new String(response.getBody().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
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
    private OCR saveOcrResult(String img, Member member, PaperInfo paperInfo, String ocrResult) {
        OCR ocrEntity = new OCR();
        ocrEntity.setImage(img);
        ocrEntity.setMember(member);
        ocrEntity.setPaperInfo(paperInfo);
        ocrEntity.setTitle("임시저장");

        try {
            Map<String, Object> ocrDataMap = new HashMap<>();
            ocrDataMap.put("ocr_text", ocrResult);
            String ocrDataJson = objectMapper.writeValueAsString(ocrDataMap);
            ocrEntity.setOcrData(new String(ocrDataJson.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            log.error("Error converting OCR result to JSON", e);
            throw new RuntimeException("Error saving OCR result.");
        }

        return ocrRepository.save(ocrEntity);
    }

    @Transactional
    public void saveOcrText(OcrRequestDto dto) {
        OCR ocr = ocrRepository.findByOcrIndex(dto.getOcrId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_INFO));
        ocr.setOcrData(dto.getOcrData());
        ocr.setTitle(dto.getTitle());

        ocrRepository.save(ocr);
    }

    public List<OcrListResponseDto> getPosts(UserDetailsImpl userDetails) {
        Member member = memberRepository.findByIndex(userDetails.getId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        List<OCR> ocrList = ocrRepository.findByMember(member);
        if (ocrList.isEmpty()) {
            throw new MemberException(NOT_FOUND_INFO);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<OcrListResponseDto> list = new ArrayList<>();
        for (OCR ocr : ocrList) {
            String formattedCreatedAt = ocr.getCreatedAt().format(formatter);
            String formattedModifyAt = ocr.getModifyAt().format(formatter);

            OcrListResponseDto dto = OcrListResponseDto.builder()
                    .resultIdx(ocr.getOcrIndex())
                    .title(ocr.getTitle())
                    .infoTitle(ocr.getPaperInfo().getTitleText())
                    .createdAt(formattedCreatedAt)
                    .modifyAt(formattedModifyAt)
                    .build();

            list.add(dto);
        }

        return list;
    }

    public OcrResponseDto getPost(Long ocrIdx) {
        OCR ocr = ocrRepository.findByOcrIndex(ocrIdx)
                .orElseThrow(() -> new MemberException(NOT_FOUND_INFO));

        return OcrResponseDto.builder()
                .ocrId(ocr.getOcrIndex())
                .title(ocr.getTitle())
                .infoTitle(ocr.getPaperInfo().getTitleText())
                .ocrResult(ocr.getOcrData())
                .createTime(ocr.getCreatedAt())
                .modifyTime(ocr.getModifyAt())
                .originalImg(ocr.getImage())
                .emptyImg(ocr.getPaperInfo().getEmptyImg())
                .build();
    }

    public void deletePost(Long ocrIdx) {
        OCR ocr = ocrRepository.findByOcrIndex(ocrIdx)
                .orElseThrow(() -> new MemberException(NOT_FOUND_INFO));

        ocrRepository.delete(ocr);
    }

    public OcrResponseDto updatePost(Long ocrIdx, OcrRequestDto dto) {
        OCR ocr = ocrRepository.findByOcrIndex(ocrIdx)
                .orElseThrow(() -> new MemberException(NOT_FOUND_INFO));

        ocr.setOcrData(dto.getOcrData());
        ocr.setTitle(dto.getTitle());

        ocrRepository.save(ocr);
        return OcrResponseDto.builder()
                .ocrResult(dto.getOcrData())
                .title(dto.getTitle())
                .build();
    }

    public boolean deleteImg(UserDetailsImpl userDetails, OcrFileNameRequest filenameDto) {
        Member member = memberRepository.findByIndex(userDetails.getId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_INFO));

        // 사용자에 연결된 모든 OCR 정보 조회
        List<OCR> ocrList = ocrRepository.findByMember(member);

        if (ocrList.isEmpty()) {
            throw new MemberException(NOT_FOUND_INFO);
        }

        // DTO의 이미지 이름과 매칭되는 OCR 정보를 찾고 삭제
        boolean isImageDeleted = false;

        for (OCR ocr : ocrList) {
            String fullImagePath = ocr.getImage(); // C:\app\image\ocr\212fb7ab-ebcb-4961-acad-1660e6813518.png
            String imageName = Paths.get(fullImagePath).getFileName().toString(); // 212fb7ab-ebcb-4961-acad-1660e6813518.png

            if (imageName.equals(filenameDto.getFileName())) {
                // 폴더 이름 추출 (예: ocr)
                String folderName = Paths.get(fullImagePath).getParent().getFileName().toString();

                // 이미지 삭제
                boolean isDeleted = localFileUtil.deleteImage(folderName, filenameDto.getFileName());

                if (isDeleted) {
                    // OCR 엔티티에서 이미지 정보 제거 (DB 업데이트)
                    ocr.setImage(null);
                    ocrRepository.save(ocr);
                    isImageDeleted = true;
                    break; // 매칭된 이미지 삭제 후 반복 종료
                } else {
                    throw new RuntimeException("Failed to delete image.");
                }
            }
        }

        if (!isImageDeleted) {
            throw new IllegalArgumentException("Image name does not match any existing records for the user.");
        }

        return true;
    }
}