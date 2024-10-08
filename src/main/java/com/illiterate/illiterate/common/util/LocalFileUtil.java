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


    @Value("${tmpfile.path}")
    private String tmpfilePath;

    @Value("${file.path}")
    private String filePath;

    @Value("${file.path.db}")
    private String savedPath;


    @Value("${IMAGE_PATH:/app/image}")
    private String imagePath;  // 기본 이미지 경로 설정

    public String saveImage(MultipartFile file) {
        if (file.isEmpty()) {
            logger.error("File is empty.");
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + extension;
        String savePath = Paths.get(imagePath, saveFileName).toAbsolutePath().toString();

        try {
            Path path = Paths.get(savePath).normalize();
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path);
            logger.debug("File saved successfully: {}", savePath);
        } catch (Exception e) {
            logger.error("Error saving image: {}", e.getMessage());
            return null;
        }

        return savePath;
    }

    public boolean deleteImage(String imagePath) {
        try {
            Path path = Paths.get(imagePath).normalize();
            Files.deleteIfExists(path);
            logger.debug("File deleted successfully: {}", imagePath);
            return true;
        } catch (IOException e) {
            logger.error("Error deleting file: {}", e.getMessage());
            return false;
        }
    }

    public String adjustImagePath(String imagePath) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return imagePath.replace("C:\\app\\image", "/app/image").replace("\\", "/");
        } else {
            if (imagePath.startsWith("/app/image")) {
                return imagePath;
            } else {
                return "/app/image/" + Paths.get(imagePath).getFileName().toString();
            }
        }
    }

}