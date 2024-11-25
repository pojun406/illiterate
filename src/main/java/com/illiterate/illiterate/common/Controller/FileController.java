package com.illiterate.illiterate.common.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class FileController {

    private final String basePath = "D:/"; // 파일 저장 경로

    @GetMapping("/app/image/ocr/{fileName}")
    public ResponseEntity<?> getImage(@PathVariable String fileName) {
        try {
            System.out.println("filename : " + fileName);
            // 파일 경로 구성
            String fullPath = Paths.get(basePath, extractFileName(fileName)).toAbsolutePath().toString();
            File file = new File(fullPath);

            // 파일 존재 여부 확인
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("파일을 찾을 수 없습니다: " + fullPath);
            }

            // 파일 데이터 읽기
            byte[] fileData = Files.readAllBytes(file.toPath());

            // 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG); // MIME 타입 설정
            headers.setContentDispositionFormData("inline", file.getName()); // 파일 이름

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 읽기 중 오류 발생: " + e.getMessage());
        }
    }

    public static String extractFileName(String fullPath) {
        return Paths.get(fullPath).getFileName().toString();
    }
}