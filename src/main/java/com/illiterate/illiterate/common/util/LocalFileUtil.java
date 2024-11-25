package com.illiterate.illiterate.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Component
public class LocalFileUtil {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileUtil.class);

    @Value("${file.path}")
    private String baseFilePath;

    private final String basePath = "D:/"; // 기본 경로

    /**
     * MultipartFile 이미지를 상대 경로로 저장
     */
    public String saveImage(MultipartFile file, String folderName) {
        if (file.isEmpty()) {
            logger.error("File is empty.");
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + extension;

        // 상대 경로 설정
        String relativePath = Paths.get(folderName, saveFileName).toString();
        String savePath = Paths.get(baseFilePath, relativePath).normalize().toString();

        try {
            // 디렉토리 생성
            Path path = Paths.get(savePath).getParent();
            if (path != null && !Files.exists(path)) {
                Files.createDirectories(path);
            }

            // 파일 저장
            Files.copy(file.getInputStream(), Paths.get(savePath));
            logger.debug("File saved successfully at relative path: {}", relativePath);
        } catch (IOException e) {
            logger.error("Error saving image: {}", e.getMessage());
            return null;
        }

        // 경로를 반환 (슬래시로 치환)
        return savePath.replace("\\", "/");
    }

    /**
     * Base64 문자열을 디코딩하여 이미지 파일로 저장하는 메서드
     */
    public String saveImageFromBase64(String title, String base64String, String folderName, String fileExtension) {
        String saveFileName = title + "." + fileExtension;

        // 저장 경로를 상대 경로로 설정
        String relativePath = "/" + folderName + "/" + saveFileName;
        String savePath = Paths.get(baseFilePath, folderName, saveFileName).toAbsolutePath().toString();

        try {
            Path path = Paths.get(savePath).normalize();
            Files.createDirectories(path.getParent());

            byte[] imageData = Base64.getDecoder().decode(base64String);
            try (FileOutputStream fos = new FileOutputStream(savePath)) {
                fos.write(imageData);
            }

            logger.debug("Base64 image saved successfully in folder {}: {}", folderName, savePath);
        } catch (IOException e) {
            logger.error("Error saving Base64 image: {}", e.getMessage());
            return null;
        }

        // 경로의 \를 /로 치환하여 반환
        return relativePath.replace("\\", "/");
    }

    public boolean deleteImage(String folderName, String fileName) {
        boolean isDeleted = false;

        // 첫 번째 경로 (/app/image)
        String appImagePath = Paths.get(baseFilePath, folderName, fileName).toAbsolutePath().toString();

        // 프로젝트 내부 경로 (src/main/resources/static/image)
        String projectImagePath = Paths.get("src/main/resources/image", folderName, fileName).toAbsolutePath().toString();

        try {
            // 첫 번째 경로의 파일 삭제
            Path appPath = Paths.get(appImagePath).normalize();
            if (Files.exists(appPath)) {
                Files.delete(appPath);
                logger.debug("File deleted from app path: {}", appImagePath);
                isDeleted = true;
            }

            // 프로젝트 내부 경로의 파일 삭제
            Path projectPath = Paths.get(projectImagePath).normalize();
            if (Files.exists(projectPath)) {
                Files.delete(projectPath);
                logger.debug("File deleted from project path: {}", projectImagePath);
                isDeleted = true;
            }

        } catch (IOException e) {
            logger.error("Error deleting file: {}", e.getMessage());
            return false;
        }

        return isDeleted;
    }

    public byte[] getFile(String relativePath) {
        try {
            // 파일 경로를 구성
            String fullPath = Paths.get(basePath, relativePath).toAbsolutePath().toString();
            File file = new File(fullPath);

            // 파일 존재 여부 확인
            if (!file.exists()) {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + fullPath);
            }

            // 파일을 읽어 바이트 배열로 반환
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 중 오류 발생: " + relativePath, e);
        }
    }
}