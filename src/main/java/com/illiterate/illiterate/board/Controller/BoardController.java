package com.illiterate.illiterate.board.Controller;



import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.board.DTO.response.BoardResponseDto;
import com.illiterate.illiterate.board.Repository.BoardRepository;
import com.illiterate.illiterate.board.Service.BoardService;
import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.Entity.User;
import com.illiterate.illiterate.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;

    @GetMapping("/posts")
    public ResponseEntity<BfResponse<List<BoardResponseDto>>> getPosts() {
        List<BoardResponseDto> posts = boardService.getPosts();
        BfResponse<List<BoardResponseDto>> response = new BfResponse<>(posts);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/posts/{id}")
    public ResponseEntity<BfResponse<BoardResponseDto>> getPost(@PathVariable Long id) {
        BoardResponseDto post = boardService.getPost(id);
        BfResponse<BoardResponseDto> response = new BfResponse<>(post);
        return ResponseEntity.ok(response);
    }

    //게시글 작성
    @PostMapping(value = "/post", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BfResponse<BoardResponseDto>> createPost(
            @Valid @RequestPart("request") BoardRequestDto requestsDto,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        requestsDto.setImage(image);
        BoardResponseDto responseDto = boardService.createPost(requestsDto, userDetails.getUser());
        return ResponseEntity.ok(new BfResponse<>(responseDto));
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponseDto> updatePost(
            @PathVariable Long id,
            @RequestPart("data") BoardRequestDto requestsDto,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        requestsDto.setImage(image);
        User user = userDetails.getUser();
        BoardResponseDto updatedPost = boardService.updatePost(id, requestsDto, user);
        return ResponseEntity.ok(updatedPost);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userDetails.getUser();
        boardService.deletePost(id, user);
        return ResponseEntity.noContent().build();
    }
}