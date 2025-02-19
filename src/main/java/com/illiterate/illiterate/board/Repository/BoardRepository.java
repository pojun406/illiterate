package com.illiterate.illiterate.board.Repository;

import com.illiterate.illiterate.board.Entity.Board;
import com.illiterate.illiterate.member.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAll();

    Optional<Board> findByBoardId(Long bid);

    List<Board> findByMemberIndex(Long userIdx);

    Optional<Board> findByMember(Member member);

}
