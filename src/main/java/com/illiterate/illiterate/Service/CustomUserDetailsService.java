package com.illiterate.illiterate.Service;

import com.illiterate.illiterate.DTO.CustomUserDetails;
import com.illiterate.illiterate.Entity.User;
import com.illiterate.illiterate.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // UserRepository를 주입받는 생성자
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Spring Security의 UserDetailsService 인터페이스를 구현하는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 이름(username)을 이용하여 데이터베이스에서 사용자 정보를 조회
        User userData = userRepository.findByUserid(username);

        // 조회된 사용자 정보가 있는 경우
        if (userData != null) {
            // 조회된 사용자 정보를 CustomUserDetails 객체로 변환하여 반환
            return new CustomUserDetails(userData);
        }

        // 조회된 사용자 정보가 없는 경우 예외를 던짐
        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
    }
}
