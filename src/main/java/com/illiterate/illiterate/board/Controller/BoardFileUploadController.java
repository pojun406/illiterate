package com.illiterate.illiterate.board.Controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BoardFileUploadController {
    private static final String UTIL_PYTHON_SCRIPT_PATH = "/pythonProject/run.py";
    private static final String SAVE_TEXT_FOLDER = "/pythonProject/savetext/";

    @PostMapping("/board/upload")
    public ResponseEntity<List<String>> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException, InterruptedException {
        // Save the file to a temporary location
        String tempDir = System.getProperty("java.io.tmpdir");
        File tempFile = new File(tempDir + "/" + file.getOriginalFilename());
        file.transferTo(tempFile);

        // Run Python script
        runPythonScript(tempFile);

        // Read JSON files
        List<String> ocrResults = readJsonFiles();

        // Delete temporary file
        Files.delete(Paths.get(tempFile.getAbsolutePath()));

        return ResponseEntity.ok(ocrResults);
    }

    private void runPythonScript(File file) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", UTIL_PYTHON_SCRIPT_PATH, file.getAbsolutePath());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // Read the output from the Python script
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        process.waitFor();
    }

    private List<String> readJsonFiles() throws IOException {
        List<String> results = new ArrayList<>();
        File folder = new File(SAVE_TEXT_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                String content = new String(Files.readAllBytes(file.toPath()));
                results.add(content);
            }
        }
        return results;
    }
}