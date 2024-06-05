package com.illiterate.illiterate.ocr.Controller;



import com.illiterate.illiterate.ocr.DTO.request.OcrRequestDto;
import com.illiterate.illiterate.ocr.DTO.response.OcrResponseDto;
import com.illiterate.illiterate.ocr.Repository.OcrRepository;
import com.illiterate.illiterate.ocr.Service.OcrService;
import com.illiterate.illiterate.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OcrController {

    private final OcrService boardService;
    private final OcrRepository boardRepository;

    @GetMapping("/api/board/posts")
    public List<OcrResponseDto> getPosts() {
        return boardService.getPosts();
    }

    @GetMapping("/api/board/posts/{id}")
    public OcrResponseDto getPost(@PathVariable Long id) {
        return boardService.getPost(id);
    }

    @PostMapping("/api/board/post")
    public OcrResponseDto createPost(@RequestBody OcrRequestDto requestsDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.createPost(requestsDto, userDetails.getUser());
    }

}