package com.illiterate.illiterate.member.Service;

import com.illiterate.illiterate.common.repository.RedisRepository;
import com.illiterate.illiterate.member.DTO.request.JoinDto;
import com.illiterate.illiterate.member.DTO.request.LoginDto;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
import com.illiterate.illiterate.member.Entity.User;
import com.illiterate.illiterate.member.Repository.UserRepository;
import com.illiterate.illiterate.member.exception.MemberException;
import com.illiterate.illiterate.security.JWT.JWTProvider;
import com.illiterate.illiterate.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Join;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.illiterate.illiterate.common.enums.MemberErrorCode.DUPLICATED_MEMBER_EMAIL;
import static com.illiterate.illiterate.common.enums.MemberErrorCode.DUPLICATED_MEMBER_PHONE_NUMBER;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisRepository redisRepository;

    /**
     * 회원 등록
     */
    @Transactional
    public int joinUser(JoinDto joinDto) {
        // 아이디 중복 체크
        if (userRepository.existsByUserid(joinDto.getUserid())) {
            throw new MemberException(DUPLICATED_MEMBER_EMAIL);
        } else if (userRepository.existsByEmail(joinDto.getEmail())) {
            throw new MemberException(DUPLICATED_MEMBER_PHONE_NUMBER);
        }

        User member = new User();
        member.setUserid(joinDto.getUserid());
        member.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        member.setEmail(joinDto.getEmail());
        member.setUsername(joinDto.getUsername());

        int id = userRepository.save(member).getId();

        return id;
    }

    public LoginTokenDto login(LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()
        );

        Authentication authenticated = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetail = (UserDetailsImpl) authenticated.getPrincipal();

        // accessToken, refreshToken 생성
        String accessToken = jwtProvider.createAccessToken(userDetail);
        String refreshToken = jwtProvider.createRefreshToken(userDetail);

        LoginTokenDto loginTokenDto = new LoginTokenDto(accessToken, refreshToken);

        // redis 토큰 정보 저장
        redisRepository.saveToken(userDetail.getId(), refreshToken);

        return loginTokenDto;
    }
}
