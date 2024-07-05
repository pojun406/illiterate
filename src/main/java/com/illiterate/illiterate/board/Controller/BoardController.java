package com.illiterate.illiterate.board.Controller;



import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.board.DTO.response.BoardResponseDto;
import com.illiterate.illiterate.board.Repository.BoardRepository;
import com.illiterate.illiterate.board.Service.BoardService;
import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.Entity.Member;
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
    // 게시글 전체 목록 조회

        /*
            response :
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
         */
    @PostMapping("/posts")
    public ResponseEntity<BfResponse<List<BoardResponseDto>>> getPosts() {
        List<BoardResponseDto> posts = boardService.getPosts();
        BfResponse<List<BoardResponseDto>> response = new BfResponse<>(posts);
        return ResponseEntity.ok(response);
    }

    /*
        request :
            "board_id" : 1(Long)
        response :
            "id": 1,
            "board_id" : 1(Long),
            "title": "First Post",
            "content": "This is the content of the first post.",
            "imagePath": "/images/first-post.jpg",
            "status": "ACTIVE"
     */
    @PostMapping("/posts/{id}")
    public ResponseEntity<BfResponse<BoardResponseDto>> getPost(@PathVariable Long id) {
        BoardResponseDto post = boardService.getPost(id);
        BfResponse<BoardResponseDto> response = new BfResponse<>(post);
        return ResponseEntity.ok(response);
    }

    /*
    "request":
        {
            "title": "New Post",
            "content": "This is the content of the new post."
        },
        "image": "imageFile"
    "Response":
        "id": 3,
        "title": "New Post",
        "content": "This is the content of the new post.",
        "imagePath": "/images/new-post.jpg",
        "status": "ACTIVE"

     */
    //게시글 작성
    @PostMapping(value = "/post", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BfResponse<BoardResponseDto>> createPost(
            @Valid @RequestPart("request") BoardRequestDto requestsDto,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        requestsDto.setImage(image);
        Member user = userDetails.getUser();
        BoardResponseDto responseDto = boardService.createPost(requestsDto, user);
        return ResponseEntity.ok(new BfResponse<>(responseDto));
    }
    /*
    "request":
        "data": {
            "board_id" : 1(Long)
            "userId" : 1(Long)
            "title": "Updated Post",
            "content": "This is the updated content of the post."
        },
        "image": "imageFile"
    "Response":
        "id": 1,
        "title": "Updated Post",
        "content": "This is the updated content of the post.",
        "imagePath": "/images/updated-post.jpg",
        "status": "ACTIVE"
     */
    // 게시글 수정
    @PostMapping("/fix_post/{id}")
    public ResponseEntity<BoardResponseDto> updatePost(
            @PathVariable Long id,
            @RequestPart("data") BoardRequestDto requestsDto,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        requestsDto.setImage(image);
        BoardResponseDto updatedPost = boardService.updatePost(id, requestsDto, userDetails.getId());
        return ResponseEntity.ok(updatedPost);
    }

    /*
    request :
        "board_id" : 1(Long)
     */
    // 게시글 삭제
    @PostMapping("/del_post/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boardService.deletePost(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}