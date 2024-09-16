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
    public String saveImageTmp(MultipartFile file){
        if(file.isEmpty()){
            logger.error("not images");
            return null;
        }
        String originalFileName = file.getOriginalFilename();
        //확장자 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + extension;
        String savePath = filePath + saveFileName;

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

    // 이미지 tmp파일에 저장
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
}