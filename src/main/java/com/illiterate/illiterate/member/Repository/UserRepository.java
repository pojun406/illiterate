package com.illiterate.illiterate.member.Repository;

import com.illiterate.illiterate.member.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUserid(String userid);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserid(String userid);
    Optional<User> findByUseridAndUsername(String userid, String username);

    //Optional<User> findByResetToken(String token);
}
