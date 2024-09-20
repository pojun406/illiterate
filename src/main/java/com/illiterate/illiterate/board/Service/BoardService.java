package com.illiterate.illiterate.board.Service;

import com.illiterate.illiterate.board.DTO.request.BoardRequestDto;
import com.illiterate.illiterate.board.DTO.response.BoardResponseDto;
import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.board.Repository.BoardRepository;
import com.illiterate.illiterate.board.enums.StatusType;
import com.illiterate.illiterate.common.enums.BoardErrorCode;
import com.illiterate.illiterate.common.enums.MemberErrorCode;
import com.illiterate.illiterate.common.util.LocalFileUtil;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.Repository.MemberRepository;
import com.illiterate.illiterate.member.exception.BoardException;
import com.illiterate.illiterate.member.exception.MemberException;
import com.illiterate.illiterate.security.Service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.illiterate.illiterate.common.enums.BoardErrorCode.*;
import static com.illiterate.illiterate.common.enums.MemberErrorCode.BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final LocalFileUtil localFileUtil;

    @Transactional(readOnly = true)
    public List<BoardResponseDto> getPosts() {
        List<Board> boardList = boardRepository.findAll();
        List<BoardResponseDto> responseDtoList = new ArrayList<>();

        for (Board board : boardList) {
            responseDtoList.add(
                    BoardResponseDto.builder()
                            .id(board.getMember().getId())
                            .title(board.getTitle())
                            .content(board.getContent())
                            .imagePath(board.getRequestImg())
                            .status(board.getStatus())
                            .build());
        }

        return responseDtoList;
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getPost(UserDetailsImpl userDetails, Long id) {
        if (!userDetails.getId().equals(id)) {
            throw new BoardException(NOT_MATCHING_INFO);
        }
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(NOT_FOUND_WRITING));

        return BoardResponseDto.builder()
                .id(board.getMember().getId())
                .title(board.getTitle())
                .content(board.getContent())
                .imagePath(board.getRequestImg())
                .status(board.getStatus())
                .build();
    }

    @Transactional
    public void createPost(UserDetailsImpl userDetails, BoardRequestDto requestsDto, MultipartFile requestImg) {
        Member member = memberRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BoardException(NOT_FOUND_WRITING));

        Board board = new Board();
        String imagePath = null;

        if (requestImg != null && !requestImg.isEmpty()) {
            imagePath = localFileUtil.saveImage(requestImg);
        }

        board.setMember(member);
        board.setContent(requestsDto.getContents());
        board.setTitle(requestsDto.getTitle());
        board.setStatus(StatusType.WAIT);
        board.setRequestImg(imagePath);
        board.setRegDate(String.valueOf(Date.from(Instant.now())));

        boardRepository.save(board);
        //return null;
    }

    @Transactional
    public void updatePost(Long board_index, BoardRequestDto requestsDto, UserDetailsImpl userDetails, MultipartFile requestImg) {
        Member member = memberRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BoardException(NOT_FOUND_USER));

        Board board = boardRepository.findByBoardId(board_index)
                .orElseThrow(() -> new BoardException(NOT_FOUND_WRITING));

        String imagePath = null;

        if (requestImg != null && !requestImg.isEmpty()) {
            imagePath = localFileUtil.saveImage(requestImg);
        }

        board.setMember(member);
        board.setContent(requestsDto.getContents());
        board.setTitle(requestsDto.getTitle());
        board.setStatus(StatusType.WAIT);
        board.setRequestImg(imagePath);
        board.setRegDate(String.valueOf(Date.from(Instant.now())));

        boardRepository.save(board);
    }
    @Transactional
    public void deletePost(Long board_index, UserDetailsImpl userDetails) {

        Board board = boardRepository.findByBoardId(board_index)
                .orElseThrow(() -> new BoardException(NOT_FOUND_WRITING));

        if ((!(board.getMember().getId().equals(userDetails.getId())))) {
            throw new MemberException(BAD_REQUEST);
        }
        boardRepository.delete(board);
    }
}
