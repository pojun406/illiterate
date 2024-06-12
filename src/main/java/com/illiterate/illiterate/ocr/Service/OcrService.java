package com.illiterate.illiterate.ocr.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.*;

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

            // OCR 결과 읽기
            List<JsonNode> ocrResults = readOcrResults(new File(tempDir + "/result.json"));

            // OCR 결과를 key:value 형식으로 처리
            Map<String, String> mappedResults = processOcrResults(ocrResults);

            // 결과를 JSON 파일로 저장
            String outputFileName = tempDir + "/" + requestDto.getUser().getId() + "_result.json";
            writeResultToJson(mappedResults, outputFileName, detectFileType(ocrResults));

            // 임시 파일 삭제
            Files.delete(Paths.get(tempFile.getAbsolutePath()));
            Files.delete(Paths.get(tempDir + "/result.json"));

            // 응답 DTO 생성
            OcrResponseDto responseDto = new OcrResponseDto();
            responseDto.setImageUrl(tempFile.getAbsolutePath());
            responseDto.setId(requestDto.getUser().getId());
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
        map.put("한글성명", Collections.singletonList(21));
        map.put("한자성명", Collections.singletonList(266));
        map.put("본한자", Collections.singletonList(264));
        map.put("출생일시", Arrays.asList(268, 38));
        map.put("등록기준지", Arrays.asList(48, 49, 50));
        map.put("주소", Arrays.asList(53, 54, 56, 57, 275));
        map.put("세대주및관계", Arrays.asList(52, 55));
        map.put("외국국적", Arrays.asList(63, 64));
        map.put("부성명", Collections.singletonList(65));
        map.put("모성명", Collections.singletonList(71));
        map.put("부주민", Collections.singletonList(66));
        map.put("모주민", Arrays.asList(73, 74));
        map.put("부등록기준지", Arrays.asList(78, 79, 287));
        map.put("모등록기준지", Arrays.asList(81, 288));
        map.put("신고인성명", Collections.singletonList(98));
        map.put("신고인주민", Arrays.asList(100, 101));
        map.put("기타자격", Collections.singletonList(103));
        map.put("신고인주소", Arrays.asList(112, 111, 113, 114));
        map.put("신고인전화", Arrays.asList(117, 119, 120));
        map.put("신고인메일", Collections.singletonList(121));
        map.put("임신주수", Collections.singletonList(147));
        map.put("신생아체중", Collections.singletonList(148));
        map.put("실결혼시작일", Arrays.asList(205, 207, 208));
        map.put("출산수", Arrays.asList(213, 214, 215, 216));
        return map;
    }

    private static Map<String, List<Integer>> createFileType2Map() {
        Map<String, List<Integer>> map = new LinkedHashMap<>();
        map.put("전입자성명", Collections.singletonList(31));
        map.put("주민앞", Collections.singletonList(36));
        map.put("주민뒤", Collections.singletonList(37));
        map.put("상단연락처", Arrays.asList(32, 33, 34));
        map.put("시도", Collections.singletonList(45));
        map.put("시군구", Collections.singletonList(46));
        map.put("세대주성명", Collections.singletonList(66));
        map.put("하단연락처", Arrays.asList(485, 64, 65));
        map.put("주소", Arrays.asList(73, 74, 78, 75, 76));
        map.put("다가구주택명칭", Arrays.asList(97, 95, 99));
        map.put("세대원성명", Arrays.asList(435, 437));
        map.put("신청인번호", Arrays.asList(443, 447, 444));
        map.put("휴대전화", Arrays.asList(439, 440, 441));
        map.put("신청인성명", Collections.singletonList(457));
        return map;
    }


    public OcrResponseDto saveOcrText(Long ocrId, String text) {
        // 텍스트를 DB에 저장하는 로직 ( 추가예정 )

        // 결과 DTO 작성
        OcrResponseDto responseDto = new OcrResponseDto();
        responseDto.setId(ocrId);
        responseDto.setText(text);

        return responseDto;
    }
}
