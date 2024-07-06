package com.illiterate.illiterate.board.Service;

import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.board.DTO.response.BoardResponseDto;
import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.board.Repository.BoardRepository;
import com.illiterate.illiterate.common.enums.BoardErrorCode;
import com.illiterate.illiterate.common.util.LocalFileUtil;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.exception.BoardException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.illiterate.illiterate.common.enums.BoardErrorCode.NOT_FOUND_WRITING;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final LocalFileUtil localFileUtil;

    @Transactional(readOnly = true)
    public List<BoardResponseDto> getPosts() {
        List<Board> boardList = boardRepository.findAll();
        List<BoardResponseDto> responseDtoList = new ArrayList<>();

        for (Board board : boardList) {
            responseDtoList.add(BoardResponseDto.from(board));
        }

        return responseDtoList;
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getPost(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(NOT_FOUND_WRITING));
        return BoardResponseDto.from(board);
    }

    @Transactional
    public BoardResponseDto createPost(BoardRequestDto requestsDto, Member user) {
        String imagePath = null;
        MultipartFile image = requestsDto.getImage();

        if (image != null && !image.isEmpty()) {
            try {
                imagePath = localFileUtil.saveImage(image);
            } catch (IOException e) {
                log.error("Image saving failed", e);
                throw new RuntimeException("이미지 저장에 실패했습니다.", e);
            }
        }

        Board board = boardRepository.save(Board.of(requestsDto, user, imagePath));
        return BoardResponseDto.from(board);
    }

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
                imagePath = localFileUtil.saveImage(image);
            } catch (IOException e) {
                log.error("Image saving failed", e);
                throw new RuntimeException("이미지 저장에 실패했습니다.", e);
            }
        }

        board.updateBoard(requestsDto, imagePath);
        return BoardResponseDto.from(board);
    }

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
