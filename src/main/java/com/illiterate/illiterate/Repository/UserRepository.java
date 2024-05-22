package com.illiterate.illiterate.Repository;

import com.illiterate.illiterate.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);

    Optional<User> findByPassword(String password);

    Boolean existsByUsername(String username);

    User findByUserid(String username);
}
