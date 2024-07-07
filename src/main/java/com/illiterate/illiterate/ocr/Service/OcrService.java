package com.illiterate.illiterate.ocr.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.common.enums.BoardErrorCode;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.exception.BoardException;
import com.illiterate.illiterate.ocr.DTO.response.OcrResponseDto;
import com.illiterate.illiterate.ocr.Entity.OCR;
import com.illiterate.illiterate.ocr.Repository.OcrRepository;
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

    @Value("${python.script.path}")
    private String pythonScriptPath;

    @Value("${python.executable.path}")
    private String pythonExecutable;

    @Value("${image.upload.dir}")
    private String imageUploadDir;

    private final OcrRepository ocrRepository;

    public OcrResponseDto uploadOCRImage(Member user, MultipartFile image) {
        try {
            // 이미지 저장
            File savedImageFile = saveImage(image, user);

            // 파이썬 코드 실행
            runPythonScript(savedImageFile.getAbsolutePath());

            // 결과 파일 확인
            File resultFile = new File(imageUploadDir, "result.json");
            if (!resultFile.exists()) {
                log.error("Result file not found: {}", resultFile.getAbsolutePath());
                throw new RuntimeException("Result file not found");
            }

            // OCR 결과 읽기
            JsonNode ocrResult = readOcrResult(resultFile);

            // OCR 결과를 key:value 형식으로 처리
            Map<String, String> mappedResults = processOcrResult(ocrResult);

            // 결과를 JSON 파일로 저장
            String title = ocrResult.get("title").asText("unknown");
            String outputFileName = imageUploadDir + "/" + title + "_" + user.getId() + ".json";
            writeResultToJson(mappedResults, outputFileName, title);

            // OCR 엔티티 생성 및 저장
            OCR ocr = new OCR();
            ocr.setUser(user);
            ocr.setImagePath(savedImageFile.getAbsolutePath());
            ocr.setResult(convertToJsonString(mappedResults));
            ocrRepository.save(ocr);

            // 임시 파일 삭제
            Files.deleteIfExists(savedImageFile.toPath());
            Files.deleteIfExists(resultFile.toPath());


            // 응답 DTO 생성
            OcrResponseDto responseDto = OcrResponseDto.builder()
                    .id(user.getId())
                    .imageUrl(savedImageFile.getAbsolutePath())
                    .ocrResults(mappedResults.toString())
                    .createdAt(String.valueOf(new Date()))
                    .build();

            return responseDto;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to process OCR", e);
        }
    }

    private File saveImage(MultipartFile image, Member user) throws IOException {
        File uploadDir = new File(imageUploadDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String newFileName = System.currentTimeMillis() + "_" + user.getId() + ".png";
        File imageFile = new File(uploadDir, newFileName);
        log.info("Saving image to: {}", imageFile.getAbsolutePath());

        image.transferTo(imageFile);

        return imageFile;
    }

    private void runPythonScript(String imagePath) throws IOException, InterruptedException {
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            log.error("Image file not found: {}", imagePath);
            throw new IOException("Image file not found: " + imagePath);
        }

        log.info("Executing Python script with image path: {}", imagePath);

        CommandLine commandLine = new CommandLine(pythonExecutable);
        commandLine.addArgument(pythonScriptPath);
        commandLine.addArgument(imagePath);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);

        try {
            int exitCode = executor.execute(commandLine);
            String output = outputStream.toString("UTF-8");
            String errorOutput = errorStream.toString("UTF-8");

            log.info("Python script executed successfully with output: {}", output);
        } catch (ExecuteException e) {
            String errorOutput = errorStream.toString("UTF-8");
            log.error("Python script execution failed with exit code {}: {}", e.getExitValue(), errorOutput);
            throw new RuntimeException("Python script execution failed. " + errorOutput, e);
        }
    }

    private JsonNode readOcrResult(File resultFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(resultFile);
    }

    private Map<String, String> processOcrResult(JsonNode ocrResult) {
        String title = ocrResult.get("title").asText("unknown");
        Map<String, List<Integer>> selectedMap;

        if ("출생신고서".equals(title)) {
            selectedMap = createFileType1Map();
        } else if ("전입신고서".equals(title)) {
            selectedMap = createFileType2Map();
        } else {
            selectedMap = new HashMap<>();
        }

        Map<String, String> resultMap = new LinkedHashMap<>();
        JsonNode texts = ocrResult.get("texts");
        if (texts.isArray()) {
            for (Map.Entry<String, List<Integer>> entry : selectedMap.entrySet()) {
                String key = entry.getKey();
                List<Integer> indexes = entry.getValue();
                StringBuilder value = new StringBuilder();

                for (int index : indexes) {
                    if (index - 1 < texts.size()) {
                        JsonNode node = texts.get(index - 1);
                        if (node != null && node.has("key")) {
                            value.append(node.get("key").asText()).append(" ");
                        } else {
                            log.warn("OCR result at index {} is null or malformed", index - 1);
                        }
                    } else {
                        log.warn("OCR result index {} out of bounds", index - 1);
                    }
                }

                if (value.length() > 0) {
                    value.setLength(value.length() - 1); // Remove trailing space
                }

                resultMap.put(key, value.toString());
            }
        } else {
            log.error("OCR result texts node is not an array");
        }
        return resultMap;
    }

    private void writeResultToJson(Map<String, String> mappedResults, String outputFileName, String title) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        resultNode.put("title", title);
        resultNode.set("texts", mapper.valueToTree(mappedResults));
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFileName), resultNode);
    }

    private static Map<String, List<Integer>> createFileType1Map() {
        Map<String, List<Integer>> map = new LinkedHashMap<>();
        addToMap(map, "한글성명", 1);
        addToMap(map, "한자성명", 2);
        addToMap(map, "본한자", 3);
        addToMap(map, "출생연도", 4);
        addToMap(map, "출생월일", 5);
        addToMap(map, "등록기준지", 6, 7, 8);
        addToMap(map, "주소", 9, 10, 11, 12, 13);
        addToMap(map, "세대주및관계", 14);
        addToMap(map, "외국국적", 15);
        addToMap(map, "부성명", 16);
        addToMap(map, "모성명", 17);
        addToMap(map, "부주민", 18);
        addToMap(map, "모주민", 19);
        addToMap(map, "부등록기준지", 20);
        addToMap(map, "모등록기준지", 21);
        addToMap(map, "신고인성명", 22);
        addToMap(map, "신고인주민", 23, 24);
        addToMap(map, "기타자격", 25);
        addToMap(map, "신고인주소", 26, 27, 28, 29);
        addToMap(map, "신고인전화", 30, 31, 32);
        addToMap(map, "신고인메일", 33);
        addToMap(map, "임신주수", 34);
        addToMap(map, "신생아체중", 35);
        addToMap(map, "실결혼시작일", 36, 37, 38);
        addToMap(map, "출산수", 39);
        addToMap(map, "출산상황", 40);
        return map;
    }

    private static Map<String, List<Integer>> createFileType2Map() {
        Map<String, List<Integer>> map = new LinkedHashMap<>();
        addToMap(map, "전입자성명", 1);
        addToMap(map, "주민앞", 2);
        addToMap(map, "주민뒤", 3);
        addToMap(map, "상단연락처", 4, 5, 6);
        addToMap(map, "시도", 7);
        addToMap(map, "시군구", 8);
        addToMap(map, "세대주성명", 9);
        addToMap(map, "하단연락처", 10, 11, 12);
        addToMap(map, "주소", 13, 14, 15, 16);
        addToMap(map, "세대주및관계", 17);
        addToMap(map, "세대원성명", 18, 19, 20, 21);
        addToMap(map, "주소이전", 22);
        addToMap(map, "이전후연락처", 23, 24, 25);
        addToMap(map, "이전주소", 26, 27, 28, 29);
        return map;
    }

    private static void addToMap(Map<String, List<Integer>> map, String key, Integer... values) {
        map.put(key, Arrays.asList(values));
    }

    public OcrResponseDto saveOcrText(Long ocrId, String text) {
        Optional<OCR> ocrResultOptional = ocrRepository.findById(ocrId);
        if (!ocrResultOptional.isPresent()) {
            throw new RuntimeException("OCR 결과를 찾을 수 없습니다.");
        }

        OCR ocrResult = ocrResultOptional.get();
        ocrResult.setResult(text);

        ocrRepository.save(ocrResult);

        OcrResponseDto responseDto = OcrResponseDto.builder()
                .id(ocrResult.getId())
                .text(text)
                .build();

        return responseDto;
    }

    private String convertToJsonString(Map<String, String> ocrResult) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(ocrResult);
    }

    public List<OcrResponseDto> getPosts() {
        List<OCR> ocrList = ocrRepository.findAll();
        List<OcrResponseDto> responseDtoList = new ArrayList<>();

        for (OCR ocr : ocrList) {
            responseDtoList.add(OcrResponseDto.from(ocr));
        }

        return responseDtoList;
    }

    // 선택된 문서 조회
    @Transactional(readOnly = true)
    public OcrResponseDto getPost(Long id) {
        OCR ocr = ocrRepository.findById(id)
                .orElseThrow(() -> new BoardException(NOT_FOUND_WRITING));

        return OcrResponseDto.from(ocr);
    }

    @Transactional
    public void deletePost(Long ocrId, Long userId) {
        OCR ocr = ocrRepository.findById(ocrId)
                .orElseThrow(() -> new BoardException(NOT_FOUND_WRITING));

        if (!ocr.getUser().getId().equals(userId)) {
            throw new BoardException(BoardErrorCode.FORBIDDEN_DELETE_WRITING);
        }

        ocrRepository.delete(ocr);
    }
}