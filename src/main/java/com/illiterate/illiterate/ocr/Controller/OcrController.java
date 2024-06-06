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

    @PostMapping(value = "/file", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BfResponse<?>> uploadWantImg(@Valid @RequestPart(value = "request") OcrRequestDto requestDto, @RequestPart(value = "file", required = false) MultipartFile Image){{
            return ResponseEntity.ok(new BfResponse<>(ocrService.uploadOCRImage(requestDto)));
        }
    }
}