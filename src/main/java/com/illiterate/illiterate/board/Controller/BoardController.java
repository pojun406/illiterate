package com.illiterate.illiterate.board.Controller;

import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.board.DTO.response.BoardPostResponseDto;
import com.illiterate.illiterate.board.DTO.response.BoardResponseDto;
import com.illiterate.illiterate.board.Service.BoardService;
import com.illiterate.illiterate.common.enums.BoardErrorCode;
import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.Repository.MemberRepository;
import com.illiterate.illiterate.member.exception.BoardException;
import com.illiterate.illiterate.security.Service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // 게시글 전체 목록 조회
    @GetMapping("/posts")
    public ResponseEntity<BfResponse<List<BoardPostResponseDto>>> getPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<BoardPostResponseDto> posts = boardService.getPosts(userDetails);
        BfResponse<List<BoardPostResponseDto>> response = new BfResponse<>(posts);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/{boardIdx}")
    public ResponseEntity<BfResponse<BoardResponseDto>> getPost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                @PathVariable("boardIdx") Long boardIdx) {
        BoardResponseDto post = boardService.getPost(userDetails, boardIdx);
        return ResponseEntity.ok(new BfResponse<>(post));
    }

    @PostMapping(value = "/post", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BfResponse<?>> createPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody BoardRequestDto requestDto,
            @RequestPart("image") MultipartFile requestImg) {

        boardService.createPost(userDetails, requestDto, requestImg);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS, "post success!"));
    }

    // 게시글 수정
    @PostMapping("/posts/update/{boardIdx}")
    public ResponseEntity<BfResponse<?>> updatePost(
            @PathVariable("boardIdx") Long boardIdx,
            @RequestBody BoardRequestDto requestsDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("image") MultipartFile requestImg) {

        boardService.updatePost(boardIdx, requestsDto, userDetails, requestImg);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS, "modify success!"));
    }

    // 게시글 삭제
    @PostMapping("/posts/delete/{boardIdx}")
    public ResponseEntity<BfResponse<?>> deletePost(
            @PathVariable("boardIdx") Long boardIdx,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        boardService.deletePost(boardIdx, userDetails);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS, "delete success!"));
    }
}
