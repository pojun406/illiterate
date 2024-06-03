package com.illiterate.illiterate.board.Controller;



import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.board.DTO.response.BoardResponseDto;
import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.board.Repository.BoardRepository;
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
    private final BoardRepository boardRepository;

    @GetMapping("/api/board/posts")
    public List<BoardResponseDto> getPosts() {
        return boardService.getPosts();
    }

    @GetMapping("/api/board/posts/{id}")
    public BoardResponseDto getPost(@PathVariable Long id) {
        return boardService.getPost(id);
    }

    @PostMapping("/api/board/post")
    public BoardResponseDto createPost(@RequestBody BoardRequestDto requestsDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.createPost(requestsDto, userDetails.getUsername());
    }

}