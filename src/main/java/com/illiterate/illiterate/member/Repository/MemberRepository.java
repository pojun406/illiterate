package com.illiterate.illiterate.member.Repository;

import com.illiterate.illiterate.member.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByUserId(String userid);

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByUserId(String userid);
    Optional<Member> findByUserIdAndUserName(String userid, String username);
    Optional<Member> findByIndex(Long index);



    //Optional<User> findByResetToken(String token);
}
