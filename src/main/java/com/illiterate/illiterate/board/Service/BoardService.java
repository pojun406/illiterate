package com.illiterate.illiterate.board.Service;

import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.board.DTO.response.BoardResponseDto;
import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.board.Repository.BoardRepository;
import com.illiterate.illiterate.common.enums.BoardErrorCode;
import com.illiterate.illiterate.common.response.ErrorResponse;
import com.illiterate.illiterate.member.Entity.User;
import com.illiterate.illiterate.member.exception.BoardException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // 작성 글 저장
        Board board = boardRepository.save(Board.of(requestsDto, user));

        // BoardResponseDto 로 변환 후 responseEntity body 에 담아 반환
        return BoardResponseDto.from(board);

    }


}
