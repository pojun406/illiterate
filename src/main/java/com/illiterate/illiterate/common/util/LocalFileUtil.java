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

    // 이미지 tmp파일에 저장
    public String saveImageTmp(MultipartFile file) {
        if (file.isEmpty()) {
            logger.error("File is empty.");
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        // 확장자 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + extension;
        String savePath = Paths.get(tmpfilePath, saveFileName).toAbsolutePath().toString();

        try {
            Path path = Paths.get(savePath).normalize();
            Files.createDirectories(path.getParent());  // 디렉토리가 없을 경우 생성
            Files.copy(file.getInputStream(), path);

            // 파일 존재 여부 확인
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

        // DB에 저장되는 값이 경로이려면 savePath, 이름값이려면 saveFileName
        return savedPath + saveFileName;  // 필요에 따라 변경 가능
    }

    // 이미지파일 image 폴더에 저장
    public String saveImage(MultipartFile file){
        if(file.isEmpty()){
            logger.error("not images");
            return null;
        }
        String originalFileName = file.getOriginalFilename();
        //확장자 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + extension;
        String savePath = tmpfilePath + saveFileName;

        try {
            Path path = Paths.get(savePath).normalize();
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path);
        } catch (Exception e){
            logger.error("error save images");
            e.printStackTrace();
            return null;
        }
        System.out.println("fileName: " + saveFileName);
        //return saveFileName;
        // DB에 저장되는 값이 경로이려면 savePath 이름값이려면 saveFileName
        //return savePath;
        return savedPath + saveFileName;
    }

    public boolean deleteImageTmp(String imagePath) {
        try {
            Path path = Paths.get(imagePath).normalize();
            Files.deleteIfExists(path);
            logger.debug("Temporary file deleted successfully: " + imagePath);
            return true;
        } catch (IOException e) {
            logger.error("Error deleting temporary file: " + e.getMessage());
            return false;
        }
    }
}