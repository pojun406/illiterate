package com.illiterate.illiterate.board.Service;

import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.board.DTO.response.BoardResponseDto;
import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.board.Repository.BoardRepository;
import com.illiterate.illiterate.common.enums.BoardErrorCode;
import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.common.response.ErrorResponse;
import com.illiterate.illiterate.common.util.LocalFileUtil;
import com.illiterate.illiterate.member.Entity.User;
import com.illiterate.illiterate.member.exception.BoardException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.illiterate.illiterate.common.enums.BoardErrorCode.NOT_FOUND_WRITING;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    private static final String IMAGE_SAVE_PATH = "C:/Program Files/Illiterate";


    @Transactional(readOnly = true)
    public List<BoardResponseDto> getPosts() {
        List<Board> boardList = boardRepository.findAll();
        List<BoardResponseDto> responseDtoList = new ArrayList<>();

        for (Board board : boardList) {
            responseDtoList.add(BoardResponseDto.from(board));
        }

        return responseDtoList;
    }

    // 선택된 게시글 조회
    @Transactional(readOnly = true)
    public BoardResponseDto getPost(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(NOT_FOUND_WRITING));

        return BoardResponseDto.from(board);
    }

    // 게시글 작성
    /*
        "id": 3,
        "title": "New Post",
        "content": "This is the content of the new post.",
        "imagePath": "/images/new-post.jpg",
        "status": "ACTIVE"
     */
    @Transactional
    public BoardResponseDto createPost(BoardRequestDto requestsDto, User user) {
        String imagePath = null;
        MultipartFile image = requestsDto.getImage();

        if (image != null && !image.isEmpty()) {
            try {
                imagePath = LocalFileUtil.saveImage(image, IMAGE_SAVE_PATH);
            } catch (IOException e) {
                log.error("Image saving failed", e);
                throw new RuntimeException("이미지 저장에 실패했습니다.", e);
            }
        }

        Board board = boardRepository.save(Board.of(requestsDto, user, imagePath));
        return BoardResponseDto.from(board);
    }

    // 게시글 수정
    /*
        "id": 1,
        "title": "Updated Post",
        "content": "This is the updated content of the post.",
        "imagePath": "/images/updated-post.jpg",
        "status": "ACTIVE"
     */
    @Transactional
    public BoardResponseDto updatePost(Long postId, BoardRequestDto requestsDto, Long userId) {
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new BoardException(NOT_FOUND_WRITING));

        if (!board.getUser().getId().equals(userId)) {
            throw new BoardException(BoardErrorCode.FORBIDDEN_UPDATE_WRITING);
        }

        String imagePath = board.getImage();
        MultipartFile image = requestsDto.getImage();

        if (image != null && !image.isEmpty()) {
            try {
                imagePath = LocalFileUtil.saveImage(image, IMAGE_SAVE_PATH);
            } catch (IOException e) {
                log.error("Image saving failed", e);
                throw new RuntimeException("이미지 저장에 실패했습니다.", e);
            }
        }

        board.updateBoard(requestsDto, imagePath);
        return BoardResponseDto.from(board);
    }

    // 게시글 삭제
    // return 되는 값 없음
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new BoardException(NOT_FOUND_WRITING));

        if (!board.getUser().getId().equals(userId)) {
            throw new BoardException(BoardErrorCode.FORBIDDEN_DELETE_WRITING);
        }

        boardRepository.delete(board);
    }
}
