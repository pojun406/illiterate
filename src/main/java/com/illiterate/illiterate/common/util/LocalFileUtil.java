package com.illiterate.illiterate.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
}