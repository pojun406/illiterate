package com.illiterate.illiterate.admin.Service;

import com.illiterate.illiterate.ocr.Entity.PaperInfo;
import com.illiterate.illiterate.ocr.Repository.PaperInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final PaperInfoRepository paperInfoRepository;

    @Value("${python.paperinfo.script.path}")
    private String makepaperinfo;

    public PaperInfo makePaperInfo(String imagePath, String title) {
        // 명령어 구성
        CommandLine commandLine = new CommandLine("python");
        commandLine.addArgument(makepaperinfo); // 파이썬 스크립트 경로
        commandLine.addArgument(imagePath);  // 이미지 경로
        commandLine.addArgument(title);  // JSON 파일 이름 전달

        // 명령어 실행을 위한 Executor 설정
        DefaultExecutor executor = new DefaultExecutor();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        executor.setStreamHandler(new PumpStreamHandler(outputStream));

        String jsonContent = null;

        try {
            // 파이썬 스크립트 실행
            int exitCode = executor.execute(commandLine);
            System.out.println("Python script executed with exit code: " + exitCode);
            System.out.println("Output: " + outputStream.toString());

            // JSON 파일 읽기
            String jsonFileName = getJsonFileName(title);  // JSON 파일명 추출
            if (Files.exists(Paths.get(jsonFileName))) {
                jsonContent = new String(Files.readAllBytes(Paths.get(jsonFileName)));
                System.out.println("JSON content: " + jsonContent);
            } else {
                System.err.println("JSON file not found: " + jsonFileName);
            }
        } catch (ExecuteException e) {
            // 파이썬 스크립트 실행 중 발생한 오류 처리
            System.err.println("Execution failed: " + e.getMessage());
        } catch (IOException e) {
            // 입출력 오류 처리
            System.err.println("IO error: " + e.getMessage());
        }

        /*// PaperInfo 엔티티에 저장 (여기서는 예시로 제목 벡터를 지정)
        PaperInfo paperInfo = new PaperInfo();
        paperInfo.setTitleText(title);
        paperInfo.setImgInfo(jsonContent); // JSON 내용을 imgInfo 필드에 저장
        paperInfo.setCreatedAt(new Date());
        paperInfo.setUpdatedAt(new Date());

        // DB에 저장
        return paperInfoRepository.save(paperInfo);*/
        return null;
    }

    // 이미지 경로에서 JSON 파일명 생성
    private String getJsonFileName(String imagePath) {
        String fileNameWithoutExtension = Paths.get(imagePath).getFileName().toString().replaceFirst("[.][^.]+$", "");
        return fileNameWithoutExtension + "_roi_data.json"; // 예: 22_roi_data.json
    }

}
