package com.illiterate.illiterate.board.Controller;

import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.board.DTO.response.BoardResponseDto;
import com.illiterate.illiterate.board.Service.BoardService;
import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.Repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final MemberRepository memberRepository;

    // 게시글 전체 목록 조회
    /*
        request: 없음
        response:
        {
            "data": [
                {
                    "id": 1,
                    "title": "First Post",
                    "content": "This is the content of the first post.",
                    "imagePath": "/images/first-post.jpg",
                    "status": "ACTIVE"
                },
                {
                    "id": 2,
                    "title": "Second Post",
                    "content": "This is the content of the second post.",
                    "imagePath": "/images/second-post.jpg",
                    "status": "ACTIVE"
                }
            ]
        }
    */
    @PostMapping("/posts")
    public ResponseEntity<BfResponse<List<BoardResponseDto>>> getPosts() {
        List<BoardResponseDto> posts = boardService.getPosts();
        BfResponse<List<BoardResponseDto>> response = new BfResponse<>(posts);
        return ResponseEntity.ok(response);
    }

    // 특정 게시글 조회
    /*
        request:
        {
            "id": 1
        }
        response:
        {
            "data": {
                "id": 1,
                "title": "First Post",
                "content": "This is the content of the first post.",
                "imagePath": "/images/first-post.jpg",
                "status": "ACTIVE"
            }
        }
    */
    @PostMapping("/posts/{id}")
    public ResponseEntity<BfResponse<BoardResponseDto>> getPost(@PathVariable Long id) {
        BoardResponseDto post = boardService.getPost(id);
        BfResponse<BoardResponseDto> response = new BfResponse<>(post);
        return ResponseEntity.ok(response);
    }

    // 게시글 작성
    /*
        request:
        {
            "request": {
                "memberId": 1,
                "title": "New Post",
                "content": "This is the content of the new post."
            },
            "image": <MultipartFile>
        }
        response:
        {
            "data": {
                "id": 3,
                "title": "New Post",
                "content": "This is the content of the new post.",
                "imagePath": "/images/new-post.jpg",
                "status": "ACTIVE"
            }
        }
    */
    @PostMapping(value = "/post", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BfResponse<BoardResponseDto>> createPost(
            @Valid @RequestPart("request") BoardRequestDto requestsDto,
            @RequestPart("image") MultipartFile image) {

        if (requestsDto.getId() == null) {
            throw new IllegalArgumentException("Member ID must not be null");
        }

        Member user = memberRepository.findById(Long.valueOf(requestsDto.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        requestsDto.setImage(image);
        BoardResponseDto responseDto = boardService.createPost(requestsDto, user);
        return ResponseEntity.ok(new BfResponse<>(responseDto));
    }

    // 게시글 수정
    /*
        request:
        {
            "data": {
                "memberId": 1,
                "title": "Updated Post",
                "content": "This is the updated content of the post."
            },
            "image": <MultipartFile>
        }
        response:
        {
            "data": {
                "id": 1,
                "title": "Updated Post",
                "content": "This is the updated content of the post.",
                "imagePath": "/images/updated-post.jpg",
                "status": "ACTIVE"
            }
        }
    */
    /*@PostMapping("/fix_post/{id}")
    public ResponseEntity<BoardResponseDto> updatePost(
            @PathVariable Long id,
            @RequestPart("data") BoardRequestDto requestsDto,
            @RequestPart("image") MultipartFile image) {

        if (requestsDto.getId() == null) {
            throw new IllegalArgumentException("Member ID must not be null");
        }

        Member user = memberRepository.findById(Long.valueOf(requestsDto.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        requestsDto.setImage(image);
        BoardResponseDto updatedPost = boardService.updatePost(id, requestsDto, user.getId());
        return ResponseEntity.ok(updatedPost);
    }*/

    // 게시글 삭제
    /*
        request:
        {
            "memberId": 1
        }
        response: 없음 (204 No Content)
    */
    /*@PostMapping("/del_post/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long boardid,
            @RequestBody BoardRequestDto requestsDto) {

        if (requestsDto.getId() == null) {
            throw new IllegalArgumentException("Member ID must not be null");
        }

        Member user = memberRepository.findById(Long.valueOf(requestsDto.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        boardService.deletePost(boardid, user.getId());
        return ResponseEntity.noContent().build();
    }*/
}
