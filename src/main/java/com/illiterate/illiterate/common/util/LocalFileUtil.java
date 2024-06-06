package com.illiterate.illiterate.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalFileUtil {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String uploadMemberProfile(String memberId, MultipartFile file) throws IOException {
        String fileName = memberId + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath);

        return filePath.toString();
    }
}
