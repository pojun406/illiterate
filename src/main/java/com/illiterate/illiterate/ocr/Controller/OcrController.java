package com.illiterate.illiterate.ocr.Controller;

import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
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

    /*
        "id": 123,
        "text": "이름 이병준 주민등록번호 123456-1234567",
        "ocrResults": ["이름", "이병준", "주민등록번호", "123456-1234567"],
        "imageUrl": "/path/to/image/file.jpg",
        "filteredText": "이병준, 123456-1234567"
     */
    @PostMapping(value = "/file", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BfResponse<OcrResponseDto>> uploadWantImg(
            @Valid @RequestPart(value = "request") OcrRequestDto requestDto,
            @RequestPart(value = "file", required = false) MultipartFile image) {
        OcrResponseDto responseDto = ocrService.uploadOCRImage(requestDto, image);
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