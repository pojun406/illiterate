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

    // 로컬 및 도커 환경에 맞춘 경로 설정
    @Value("${tmpfile.path}")
    private String tmpfilePath;

    @Value("${file.path}")
    private String filePath;

    @Value("${file.path.db}")
    private String savedPath;

    @Value("${IMAGE_PATH:/app/image}")
    private String imagePath;  // 도커 환경에서 사용하는 이미지 경로

    // 이미지 tmp파일에 저장
    public String saveImageTmp(MultipartFile file) {
        if (file.isEmpty()) {
            logger.error("File is empty.");
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + extension;
        String savePath = Paths.get(tmpfilePath, saveFileName).toAbsolutePath().toString();

        try {
            Path path = Paths.get(savePath).normalize();
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path);

            if (!Files.exists(path)) {
                logger.error("File not found after saving: " + savePath);
                return null;
            }

            logger.debug("File saved successfully: " + savePath);
        } catch (Exception e) {
            logger.error("Error saving image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return savedPath + saveFileName;
    }

    // 이미지파일을 지정된 폴더에 저장
    public String saveImage(MultipartFile file) {
        if (file.isEmpty()) {
            logger.error("File is empty.");
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + extension;
        String savePath = Paths.get(imagePath, saveFileName).toAbsolutePath().toString(); // 도커 환경에 맞춘 경로

        try {
            Path path = Paths.get(savePath).normalize();
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path);

            logger.debug("File saved successfully: " + savePath);
        } catch (Exception e) {
            logger.error("Error saving image: " + e.getMessage());
            return null;
        }

        return savePath;
    }

    // 이미지 경로를 도커 및 로컬 환경에 맞게 조정
    public String adjustImagePath(String imagePath) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return imagePath.replace("C:\\app\\image", "/app/image").replace("\\", "/");
        } else if (imagePath.startsWith("/app/image")) {
            return imagePath;
        } else {
            return "/app/image/" + Paths.get(imagePath).getFileName().toString();
        }
    }
}