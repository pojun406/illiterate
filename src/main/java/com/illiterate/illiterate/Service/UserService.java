package com.illiterate.illiterate.Service;

import com.illiterate.illiterate.DTO.UserDto;
import com.illiterate.illiterate.Entity.User;
import com.illiterate.illiterate.JWT.JwtProvider;
import com.illiterate.illiterate.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserService {

    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    public User registerUser(UserDto userDto) {
        // 비밀번호를 암호화하여 저장
        User user = new User();
        user.setUid(userDto.getUserid());
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setPhonenum(userDto.getPhonenum());
        return userRepository.save(user);
    }

    public String generateToken(String username) {
        String token = jwtProvider.generateToken(username);
        LOGGER.info("Generated Token: " + token);
        return token;
    }

    public String authenticate(String userid, String password) {
        LOGGER.info("Authenticating user: " + userid);
        Optional<User> optionalUser = userRepository.findByUid(userid);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = generateToken(userid);
                LOGGER.info("Authentication successful for user: " + userid);
                return token;
            } else {
                LOGGER.warning("Password mismatch for user: " + userid);
            }
        } else {
            LOGGER.warning("User not found: " + userid);
        }
        return null; // 인증 실패
    }

    public void userProcess(User user) {
        // TODO: 추가 로직 구현
    }
}
