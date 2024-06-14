package com.illiterate.illiterate.member.Service;

import com.illiterate.illiterate.common.repository.RedisRepository;
import com.illiterate.illiterate.common.util.ConvertUtil;
import com.illiterate.illiterate.member.DTO.request.JoinDto;
import com.illiterate.illiterate.member.DTO.request.LoginDto;
import com.illiterate.illiterate.member.DTO.request.UserPasswordResetRequestDto;
import com.illiterate.illiterate.member.DTO.request.UserUpdateRequestDto;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
import com.illiterate.illiterate.member.DTO.response.UserInfoDto;
import com.illiterate.illiterate.member.Entity.User;
import com.illiterate.illiterate.member.Repository.UserRepository;
import com.illiterate.illiterate.member.enums.RolesType;
import com.illiterate.illiterate.member.exception.MemberException;
import com.illiterate.illiterate.security.JWT.JWTProvider;
import com.illiterate.illiterate.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Join;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.illiterate.illiterate.common.enums.MemberErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisRepository redisRepository;
    private final JavaMailSender mailSender;


    /**
     * 회원 등록
     */
    @Transactional
    public Long joinUser(JoinDto joinDto) {
        // 아이디 중복 체크
        if (userRepository.existsByUserid(joinDto.getUserid())) {
            throw new MemberException(DUPLICATED_MEMBER_EMAIL);
        } else if (userRepository.existsByEmail(joinDto.getEmail())) {
            throw new MemberException(DUPLICATED_MEMBER_PHONE_NUMBER);
        }

        User member = ConvertUtil.toDtoOrEntity(joinDto, User.class);
        member.setUserid(joinDto.getUserid());
        member.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        member.setEmail(joinDto.getEmail());
        member.setUsername(joinDto.getUsername());

        Long id = userRepository.save(member).getId();

        return id;
    }
    public boolean checkId(String userid){
        return userRepository.existsByUserid(userid);
    }

    public LoginTokenDto login(LoginDto memberloginDto) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                memberloginDto.getUserid(),
                memberloginDto.getPassword()
        );

        Authentication authenticated = authenticationManager.authenticate(authentication);

        System.out.println("after auth : " + authentication);

        UserDetailsImpl userDetail = (UserDetailsImpl) authenticated.getPrincipal();

        // accessToken, refreshToken 생성
        String accessToken = jwtProvider.createAccessToken(userDetail);
        String refreshToken = jwtProvider.createRefreshToken(userDetail);

        //LoginTokenDto loginTokenDto = new LoginTokenDto(accessToken, refreshToken, userDetail.getId());

        LoginTokenDto loginTokenDto = LoginTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(userDetail.getId())
                .build();

        // redis 토큰 정보 저장
        redisRepository.saveToken(userDetail.getId(), refreshToken);

        return loginTokenDto;
    }

    // 비밀번호 초기화
    public void resetPassword(UserDetailsImpl userDetails, Long memberId, UserPasswordResetRequestDto resetRequestDto) {
        // 본인 or 관리자 권한 확인
        if (!userDetails.getAuthorities().contains(RolesType.ROLE_ADMIN)
                && !userDetails.getUserid().equals(memberId)) {
            throw new MemberException(FORBIDDEN_RESET_PASSWORD);
        }

        // 회원 조회
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        member.resetPassword(passwordEncoder.encode(resetRequestDto.newPassword()));
    }

    // refresh토큰 재발급
    public LoginTokenDto refreshToken(String oldRefreshToken, Long memberId) {
        System.out.println("Old Refresh Token: " + oldRefreshToken);

        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        if (!redisRepository.hasKey(member.getId())) {
            throw new MemberException(NOT_FOUND_REFRESH_TOKEN);
        }

        Map<String, String> storedRefreshTokenMap = redisRepository.getRefreshToken(member.getId());
        String storedRefreshToken = storedRefreshTokenMap.get("refreshToken");
        //System.out.println("레디스에 저장된 refresh 토큰정보 : " + storedRefreshToken);

        /*if (!storedRefreshToken.equals(oldRefreshToken)) {
            System.out.println("토큰 불일치: ");
            System.out.println("oldRefreshToken: " + oldRefreshToken);
            System.out.println("storedRefreshToken: " + storedRefreshToken);
            throw new MemberException(NOT_MATCH_REFRESH_TOKEN);
        }*/

        UserDetailsImpl userDetail = (UserDetailsImpl) UserDetailsImpl.from(member);

        String accessToken = jwtProvider.createAccessToken(userDetail);
        String newRefreshToken = jwtProvider.createRefreshToken(userDetail);

        LoginTokenDto loginTokenDto = LoginTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .id(userDetail.getId())
                .build();

        redisRepository.saveToken(userDetail.getId(), newRefreshToken);

        return loginTokenDto;
    }


    public UserInfoDto getUserInfo(Long userId) {
        // 회원 조회
        User member = userRepository.findById(userId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        return UserInfoDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getUsername())
                .build();
    }

    public String findMemberId(String email) {
        return userRepository.findByEmail(email)
                .map(User::getUserid)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_EMAIL));
    }

    @Transactional
    public void sendPasswordResetLink(String id, String name) {
        User member = userRepository.findByUseridAndUsername(id, name)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_EMAIL));

        String resetToken = generateResetToken();
        member.setResetToken(resetToken);
        userRepository.save(member);

        String resetUrl = "http://yourdomain.com/reset-password?token=" + resetToken; // 도메인 설정해줘야함

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(member.getEmail());
        message.setSubject("비밀번호 재설정");
        message.setText("비밀번호 재설정을 위해 아래 링크를 눌러주세요\n" + resetUrl);

        mailSender.send(message);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User member = userRepository.findByResetToken(token)
                .orElseThrow(() -> new MemberException(TOKEN_EXPIRATION));

        member.setPassword(passwordEncoder.encode(newPassword));
        member.setResetToken(null);
        userRepository.save(member);
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    public void updateUserInfo(UserDetailsImpl userDetails, Long memberId, UserUpdateRequestDto userUpdateDto) {
        // 현재 로그인된 유저와 수정하려는 회원이 일치하는지 확인
        if (!userDetails.getId().equals(memberId)) {
            throw new MemberException(BAD_REQUEST);
        }

        // 회원 조회
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // 이름 업데이트
        Optional.ofNullable(userUpdateDto.name()).ifPresent(member::updateName);

        // 이메일 변경
        Optional.ofNullable(userUpdateDto.email()).ifPresent(member::updateEmail);

        userRepository.save(member);
    }

    public void inactiveMember(UserDetailsImpl userDetails, Long memberId) {
        // 회원 조회
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // 현재 로그인된 유저와 탈퇴하려는 회원이 일치하는지 확인 (관리자는 예외)
        if (member.getRoles() != RolesType.ROLE_ADMIN
                && !userDetails.getId().equals(memberId)) {
            throw new MemberException(FORBIDDEN_DELETE_MEMBER);
        }

        member.inactivateUser();
    }

    // UserService.java
    public User getUserFromToken(String token) {
        Long userId = jwtProvider.getUserIdFromToken(token);
        return userRepository.findById(userId).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));
    }
}
