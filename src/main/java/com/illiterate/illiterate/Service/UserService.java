package com.illiterate.illiterate.Service;

import com.illiterate.illiterate.Entity.User;
import com.illiterate.illiterate.JWT.JwtProvider;
import com.illiterate.illiterate.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    public User registerUser(User UserDTO) {
        // 비밀번호를 암호화하여 저장
        User user = new User();
        user.setUid(UserDTO.getUid());
        user.setPassword(passwordEncoder.encode(UserDTO.getPassword()));
        return userRepository.save(user);
    }

    public String generateToken(String username) {
        return jwtProvider.generateToken(username);
    }

    public String authenticate(String uid, String password) {
        Optional<User> userId = userRepository.findByUid(uid);
        Optional<User> userPw = userRepository.findByPassword(password);
        if (userId.isPresent() && userPw.isPresent()){
            String userid = uid;
            return generateToken(userid); // 인증 성공
        } else{
            return null; // 인증 실패
        }
    }

    public void userProcess(User user) {
    }
}

