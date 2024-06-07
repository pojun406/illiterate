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

    // 게시글 전체 목록 조회
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getPosts() {

        List<Board> boardList = boardRepository.findAll();
        List<BoardResponseDto> responseDtoList = new ArrayList<>();

        for (Board board : boardList) {
            // List<BoardResponseDto> 로 만들기 위해 board 를 BoardResponseDto 로 만들고, list 에 dto 를 하나씩 넣는다.
            responseDtoList.add(BoardResponseDto.from(board));
        }

        return responseDtoList;
    }

    // 선택된 게시글 조회
    @Transactional(readOnly = true)
    public BoardResponseDto getPost(Long id) {
        // Id에 해당하는 게시글이 있는지 확인
        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty()) { // 해당 게시글이 없다면
            throw new BoardException(NOT_FOUND_WRITING);
        }

        // board 를 responseDto 로 변환 후, ResponseEntity body 에 dto 담아 리턴
        return BoardResponseDto.from(board.get());
    }

    // 게시글 작성
    @Transactional
    public BoardResponseDto createPost(BoardRequestDto requestsDto, User user) {
        String imagePath = null;
        try {
            if (requestsDto.getImage() != null) {
                imagePath = LocalFileUtil.saveImage(requestsDto.getImage(), IMAGE_SAVE_PATH);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 저장에 실패했습니다.");
        }

        Board board = boardRepository.save(Board.of(requestsDto, user, imagePath));
        return BoardResponseDto.from(board);
    }


}
