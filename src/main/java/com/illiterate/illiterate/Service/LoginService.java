package com.illiterate.illiterate.Service;

import com.illiterate.illiterate.Entity.User;
import com.illiterate.illiterate.Repository.UserRepository;

import com.illiterate.illiterate.Entity.User;
import com.illiterate.illiterate.JWT.JWTUtil;
import com.illiterate.illiterate.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public LoginService(UserRepository userRepository, JWTUtil jwtUtil, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public String loginUser(String id, String password) {
        User user = userRepository.findByUserid(id);

        if (user != null && bCryptPasswordEncoder.matches(password, user.getPassword())) {
            // 인증에 성공한 경우 JWT 토큰 생성
            return jwtUtil.createJwt(user.getUserid(), user.getRole(), 36000L); // 여기서 expirationTime은 토큰의 만료 시간입니다.
        } else {
            // 인증에 실패한 경우
            return null;
        }
    }
}
