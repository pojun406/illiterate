package com.illiterate.illiterate.board.Controller;



import com.illiterate.illiterate.board.DTO.response.BoardResponseDto;
import com.illiterate.illiterate.board.Service.BoardService;
import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
import com.illiterate.illiterate.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/api/board/posts")
    public ResponseEntity<BfResponse<List<BoardResponseDto>>> getPosts() {
        return ResponseEntity.ok(new BfResponse<>(boardService.getPosts()));
    }

    @GetMapping("/api/board/posts/{id}")
    public ResponseEntity<BfResponse<BoardResponseDto>> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(new BfResponse<>(boardService.getPost(id)));
    }


}