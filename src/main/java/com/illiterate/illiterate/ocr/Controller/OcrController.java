package com.illiterate.illiterate.ocr.Controller;

import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
import com.illiterate.illiterate.member.Entity.User;
import com.illiterate.illiterate.member.Service.UserService;
import com.illiterate.illiterate.ocr.DTO.request.OcrRequestDto;
import com.illiterate.illiterate.ocr.DTO.response.OcrResponseDto;
import com.illiterate.illiterate.ocr.Service.OcrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.CREATE;


@RestController
@RequiredArgsConstructor
@RequestMapping("/ocr")
public class OcrController {

    private final OcrService ocrService;
    private final UserService userService;

    /*
        "id": 123
        "imagePath" : "이미지저장경로"
     */
    @PostMapping(value = "/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BfResponse<OcrResponseDto>> uploadWantImg(
            @RequestHeader("Authorization") String token,
            @RequestPart("file") MultipartFile image) {
        // 토큰에서 "Bearer " 접두사를 제거
        String accessToken = token.replace("Bearer ", "");
        User user = userService.getUserFromToken(accessToken);

        OcrResponseDto responseDto = ocrService.uploadOCRImage(user, image);
        System.out.println("유저정보랑 이미지가 있는지 확인용 : " + responseDto);
        return ResponseEntity.ok(new BfResponse<>(responseDto));
    }

    /*
        "id": 123,
        "text": "저장된 텍스트 내용"
     */
    @PostMapping(value = "/saveText")
    public ResponseEntity<BfResponse<OcrResponseDto>> saveText(
            @RequestParam Long ocrId,
            @RequestParam String text) {
        OcrResponseDto responseDto = ocrService.saveOcrText(ocrId, text);
        return ResponseEntity.ok(new BfResponse<>(responseDto));
    }
}