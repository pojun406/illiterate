package com.illiterate.illiterate.Service;

import com.illiterate.illiterate.DTO.JoinDto;
import com.illiterate.illiterate.Entity.User;
import com.illiterate.illiterate.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDto joinDTO) {

        String userid = joinDTO.getUserid();
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();
        String phonenum = joinDTO.getPhonenum();

        Boolean isExist = userRepository.existsByUserid(userid);

        if (isExist) {

            return;
        }

        User data = new User();

        data.setUsername(username);
        data.setUserid(userid);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setPhonenum(phonenum);
        data.setRole("ROLE_ADMIN");

        userRepository.save(data);
    }
}