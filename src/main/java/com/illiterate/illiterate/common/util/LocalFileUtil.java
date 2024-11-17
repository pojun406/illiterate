package com.illiterate.illiterate.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    private String filePath;

    public String saveImage(MultipartFile file, String folderName) {
        if (file.isEmpty()) {
            logger.error("File is empty.");
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + extension;

        logger.debug("Configured file path: {}", filePath);  // 환경 설정 확인용 로그

        // 저장 경로 설정
        String savePath = Paths.get(filePath, folderName, saveFileName).toAbsolutePath().toString();

        // 프로젝트 내의 image 폴더 저장 경로 설정
        String projectImagePath = Paths.get("src/main/resources/static/image", folderName, saveFileName).toAbsolutePath().toString();

        try {
            Path path = Paths.get(savePath).normalize();
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path);

            logger.debug("File saved successfully in folder {}: {}", folderName, savePath);

            // 프로젝트 내의 경로에도 파일 저장
            Path projectPath = Paths.get(projectImagePath).normalize();
            Files.createDirectories(projectPath.getParent());
            Files.copy(file.getInputStream(), projectPath);
            logger.debug("File copied successfully to project path: {}", projectImagePath);
        } catch (IOException e) {
            logger.error("Error saving image: {}", e.getMessage());
            return null;
        }

        return savePath;
    }

    /**
     * Base64 문자열을 디코딩하여 이미지 파일로 저장하는 메서드
     */
    public String saveImageFromBase64(String title, String base64String, String folderName, String fileExtension) {
        String saveFileName = title + "." + fileExtension;

        // 저장 경로를 상대 경로로 설정
        String relativePath = "/" + folderName + "/" + saveFileName;
        String savePath = Paths.get(filePath, folderName, saveFileName).toAbsolutePath().toString();

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
        String appImagePath = Paths.get(filePath, folderName, fileName).toAbsolutePath().toString();

        // 프로젝트 내부 경로 (src/main/resources/static/image)
        String projectImagePath = Paths.get("src/main/resources/static/image", folderName, fileName).toAbsolutePath().toString();

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
}