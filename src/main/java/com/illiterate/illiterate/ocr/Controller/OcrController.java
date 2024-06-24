package com.illiterate.illiterate.ocr.Controller;

import com.illiterate.illiterate.board.DTO.response.BoardResponseDto;
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

import java.util.List;
import java.util.Map;

import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.CREATE;


@RestController
@RequiredArgsConstructor
@RequestMapping("/ocr")
public class OcrController {

    private final OcrService ocrService;
    private final UserService userService;

    /*
        request :
            "file": "imageFile"
        response :
            "id": 123,
            "imageUrl": "/경로/image.png", <- DB에 저장할 이미지의 경로와 이름
            "ocrResults": [
                "/경로/result.json" <- 백단 내에서만 사용되고 삭제될거임
            ]
     */
    @PostMapping(value = "/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BfResponse<OcrResponseDto>> uploadWantImg(
            @RequestHeader("Authorization") String token,
            @RequestPart("file") MultipartFile image) {
        // 토큰에서 "Bearer " 접두사를 제거
        String accessToken = token.replace("Bearer ", "");
        User user = userService.getUserFromToken(accessToken);

        OcrResponseDto responseDto = ocrService.uploadOCRImage(user, image);
        return ResponseEntity.ok(new BfResponse<>(responseDto));
    }

    /*
        request :
            "id": 123,
            "text": "저장된 텍스트 내용"
        response :
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
    /*
        request :
            userId : (Long)
        response :
            "data": [
            {
                "id": 1,
                "title": "출생신고서",
                "content": "This is the content of the first post.",
                "imagePath": "/images/first-post.jpg",
                "status": "ACTIVE"
            },
            {
                "id": 2,
                "title": "전입신고서",
                "content": "This is the content of the second post.",
                "imagePath": "/images/second-post.jpg",
                "status": "ACTIVE"
            }
        ]
             */
    @PostMapping("/posts")
    public ResponseEntity<BfResponse<List<OcrResponseDto>>> getPosts() {
        List<OcrResponseDto> posts = ocrService.getPosts();
        BfResponse<List<OcrResponseDto>> response = new BfResponse<>(posts);
        return ResponseEntity.ok(response);
    }
}