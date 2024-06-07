package com.illiterate.illiterate.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalFileUtil {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public static String saveImage(MultipartFile file, String directory) throws IOException {
        // 디렉토리가 존재하지 않으면 생성
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 파일 저장
        String filePath = directory + "/" + file.getOriginalFilename();
        File dest = new File(filePath);
        file.transferTo(dest);

        return filePath;
    }
}
