package com.illiterate.illiterate.ocr.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.illiterate.illiterate.member.Entity.User;
import com.illiterate.illiterate.ocr.DTO.response.OcrResponseDto;
import com.illiterate.illiterate.ocr.Entity.OCR;
import com.illiterate.illiterate.ocr.Repository.OcrRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {

    private static final String UTIL_PYTHON_SCRIPT_PATH = "/pythonProject/letsgopaddle.py";

    private final OcrRepository ocrRepository;

    public OcrResponseDto uploadOCRImage(User user, MultipartFile image) {
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            File tempFile = new File(tempDir + "/" + image.getOriginalFilename());
            image.transferTo(tempFile);

            // 파이썬 코드 실행
            runPythonScript(tempFile);

            // OCR 결과 읽기
            List<JsonNode> ocrResults = readOcrResults(new File(tempDir + "/result.json"));

            // OCR 결과를 key:value 형식으로 처리
            Map<String, String> mappedResults = processOcrResults(ocrResults);

            // 결과를 JSON 파일로 저장
            String outputFileName = tempDir + "/" + user.getId() + "_result.json";
            writeResultToJson(mappedResults, outputFileName, detectFileType(ocrResults));

            // 임시 파일 삭제
            Files.delete(Paths.get(tempFile.getAbsolutePath()));
            Files.delete(Paths.get(tempDir + "/result.json"));

            // 응답 DTO 생성
            OcrResponseDto responseDto = new OcrResponseDto();
            responseDto.setImageUrl(tempFile.getAbsolutePath());
            responseDto.setId(user.getId());
            responseDto.setOcrResults(Collections.singletonList(outputFileName));

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

    private List<JsonNode> readOcrResults(File resultFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(resultFile);
        List<JsonNode> ocrResults = new ArrayList<>();
        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                ocrResults.add(node);
            }
        }
        return ocrResults;
    }

    private Map<String, String> processOcrResults(List<JsonNode> ocrResults) {
        int detectedFileType = detectFileType(ocrResults);
        Map<String, List<Integer>> selectedMap = detectedFileType == 1 ? createFileType1Map() : createFileType2Map();

        Map<String, String> resultMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<Integer>> entry : selectedMap.entrySet()) {
            String key = entry.getKey();
            List<Integer> indexes = entry.getValue();
            StringBuilder value = new StringBuilder();

            for (int index : indexes) {
                if (index - 1 < ocrResults.size()) {
                    value.append(ocrResults.get(index - 1).get(1).get(0).asText()).append(", ");
                }
            }

            // 마지막 콤마 및 공백 제거
            if (value.length() > 0) {
                value.setLength(value.length() - 2);
            }

            resultMap.put(key, value.toString());
        }
        return resultMap;
    }

    private int detectFileType(List<JsonNode> ocrResults) {
        final String keywordFileType1 = "'생'신'고";
        final String keywordFileType2 = "전입신고서세대";

        for (JsonNode result : ocrResults) {
            String text = result.get(1).get(0).asText();
            if (text.contains(keywordFileType1)) {
                return 1;
            } else if (text.contains(keywordFileType2)) {
                return 2;
            }
        }
        return 0;
    }

    private void writeResultToJson(Map<String, String> mappedResults, String outputFileName, int detectedFileType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        resultNode.put("title", detectedFileType == 1 ? "출생신고서" : "전입신고서");
        resultNode.set("texts", mapper.valueToTree(mappedResults));
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFileName), resultNode);
    }

    private static Map<String, List<Integer>> createFileType1Map() {
        Map<String, List<Integer>> map = new LinkedHashMap<>();
        map.put("한글성명", Collections.singletonList(1));
        map.put("한자성명", Collections.singletonList(2));
        map.put("본한자", Collections.singletonList(3));
        map.put("출생연도", Collections.singletonList(4));
        map.put("출생월일", Collections.singletonList(5));
        map.put("등록기준지1", Collections.singletonList(6));
        map.put("등록기준지2", Collections.singletonList(7));
        map.put("등록기준지3", Collections.singletonList(8));
        map.put("주소1", Collections.singletonList(9));
        map.put("주소2", Collections.singletonList(10));
        map.put("주소3", Collections.singletonList(11));
        map.put("주소4", Collections.singletonList(12));
        map.put("주소5", Collections.singletonList(13));
        map.put("세대주및관계", Collections.singletonList(14));
        map.put("외국국적", Collections.singletonList(15));
        map.put("부성명", Collections.singletonList(16));
        map.put("모성명", Collections.singletonList(17));
        map.put("부주민", Collections.singletonList(18));
        map.put("모주민", Collections.singletonList(19));
        map.put("부등록기준지", Collections.singletonList(20));
        map.put("모등록기준지", Collections.singletonList(21));
        map.put("신고인성명", Collections.singletonList(22));
        map.put("신고인주민1", Collections.singletonList(23));
        map.put("신고인주민2", Collections.singletonList(24));
        map.put("기타자격", Collections.singletonList(25));
        map.put("신고인주소1", Collections.singletonList(26));
        map.put("신고인주소2", Collections.singletonList(27));
        map.put("신고인주소3", Collections.singletonList(28));
        map.put("신고인주소4", Collections.singletonList(29));
        map.put("신고인전화1", Collections.singletonList(30));
        map.put("신고인전화2", Collections.singletonList(31));
        map.put("신고인전화3", Collections.singletonList(32));
        map.put("신고인메일", Collections.singletonList(33));
        map.put("임신주수", Collections.singletonList(34));
        map.put("신생아체중", Collections.singletonList(35));
        map.put("실결혼시작일1", Collections.singletonList(36));
        map.put("실결혼시작일2", Collections.singletonList(37));
        map.put("실결혼시작일3", Collections.singletonList(38));
        map.put("출산수", Collections.singletonList(39));
        map.put("출산상황", Collections.singletonList(40));
        return map;
    }

    private static Map<String, List<Integer>> createFileType2Map() {
        Map<String, List<Integer>> map = new LinkedHashMap<>();
        map.put("전입자성명", Collections.singletonList(1));
        map.put("주민앞", Collections.singletonList(2));
        map.put("주민뒤", Collections.singletonList(3));
        map.put("상단연락처1", Collections.singletonList(4));
        map.put("상단연락처2", Collections.singletonList(5));
        map.put("상단연락처3", Collections.singletonList(6));
        map.put("시도", Collections.singletonList(7));
        map.put("시군구", Collections.singletonList(8));
        map.put("세대주성명", Collections.singletonList(9));
        map.put("하단연락처1", Collections.singletonList(10));
        map.put("하단연락처2", Collections.singletonList(11));
        map.put("하단연락처3", Collections.singletonList(12));
        map.put("주소1", Collections.singletonList(13));
        map.put("주소2", Collections.singletonList(14));
        map.put("주소3", Collections.singletonList(15));
        map.put("주소4", Collections.singletonList(16));
        map.put("세대주및관계", Collections.singletonList(17));
        map.put("세대원성명1", Collections.singletonList(18));
        map.put("세대원성명2", Collections.singletonList(19));
        map.put("세대원성명3", Collections.singletonList(20));
        map.put("세대원성명4", Collections.singletonList(21));
        map.put("주소이전", Collections.singletonList(22));
        map.put("이전후연락처1", Collections.singletonList(23));
        map.put("이전후연락처2", Collections.singletonList(24));
        map.put("이전후연락처3", Collections.singletonList(25));
        map.put("이전주소1", Collections.singletonList(26));
        map.put("이전주소2", Collections.singletonList(27));
        map.put("이전주소3", Collections.singletonList(28));
        map.put("이전주소4", Collections.singletonList(29));
        return map;
    }

    public OcrResponseDto saveOcrText(Long ocrId, String text) {
        Optional<OCR> ocrResultOptional = ocrRepository.findById(ocrId);
        if (!ocrResultOptional.isPresent()) {
            throw new RuntimeException("OCR 결과를 찾을 수 없습니다.");
        }

        OCR ocrResult = ocrResultOptional.get();
        ocrResult.setResult(text); // JSON 데이터를 문자열로 저장

        ocrRepository.save(ocrResult);

        OcrResponseDto responseDto = new OcrResponseDto();
        responseDto.setId(ocrResult.getId());
        responseDto.setText(text);

        return responseDto;
    }

    private String convertToJsonString(Map<String, String> ocrResult) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(ocrResult);
    }
}