package com.illiterate.illiterate.member.Repository;

import com.illiterate.illiterate.member.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserid(String userid);

    boolean existsByUserId(String userid);

    boolean existsByPhoneNumber(String phonenum);

    Optional<User> findByNameAndPhoneNumber(String username, String phonenum);
}
