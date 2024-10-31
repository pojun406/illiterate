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

        try {
            Path path = Paths.get(savePath).normalize();
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path);

            logger.debug("File saved successfully in folder {}: {}", folderName, savePath);
        } catch (IOException e) {
            logger.error("Error saving image: {}", e.getMessage());
            return null;
        }

        return savePath;
    }

    /**
     * Base64 문자열을 디코딩하여 이미지 파일로 저장하는 메서드
     */
    public String saveImageFromBase64(String base64String, String folderName, String fileExtension) {
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + "." + fileExtension;

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
}