package com.illiterate.illiterate.board.Controller;

import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
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
    private final MemberRepository memberRepository;

    // 게시글 전체 목록 조회
    @PostMapping("/public/posts")
    public ResponseEntity<BfResponse<List<BoardResponseDto>>> getPosts() {
        List<BoardResponseDto> posts = boardService.getPosts();
        BfResponse<List<BoardResponseDto>> response = new BfResponse<>(posts);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/posts/{id}")
    public ResponseEntity<BfResponse<BoardResponseDto>> getPost(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        BoardResponseDto post = boardService.getPost(userDetails, id);
        BfResponse<BoardResponseDto> response = new BfResponse<>(post);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/user/post", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BfResponse<?>> createPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody BoardRequestDto requestDto) {

        boardService.createPost(userDetails, requestDto);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS, "post success!"));
    }

    // 게시글 수정
    @PostMapping("/user/fix_post/{board_index}")
    public ResponseEntity<BfResponse<?>> updatePost(
            @PathVariable Long board_index,
            @RequestBody BoardRequestDto requestsDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        boardService.updatePost(board_index, requestsDto, userDetails);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS, "modify success!"));
    }

    // 게시글 삭제
    @PostMapping("/user/del_post/{board_index}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long board_index,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        boardService.deletePost(board_index, userDetails);
        return ResponseEntity.noContent().build();
    }
}
