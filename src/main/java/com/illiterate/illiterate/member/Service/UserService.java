package com.illiterate.illiterate.member.Service;

import com.illiterate.illiterate.common.repository.RedisRepository;
import com.illiterate.illiterate.member.DTO.request.JoinDto;
import com.illiterate.illiterate.member.DTO.request.LoginDto;
import com.illiterate.illiterate.member.DTO.request.UserPasswordResetRequestDto;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
import com.illiterate.illiterate.member.Entity.User;
import com.illiterate.illiterate.member.Repository.UserRepository;
import com.illiterate.illiterate.member.enums.RolesType;
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

    /**
     * 회원 등록
     */
    @Transactional
    public long joinUser(JoinDto joinDto) {
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

        long id = userRepository.save(member).getId();

        return id;
    }

    public LoginTokenDto login(LoginDto memberloginDto) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                memberloginDto.getUserid(),
                memberloginDto.getPassword()
        );

        Authentication authenticated = authenticationManager.authenticate(authentication);

        UserDetailsImpl userDetail = (UserDetailsImpl) authenticated.getPrincipal();

        // accessToken, refreshToken 생성
        String accessToken = jwtProvider.createAccessToken(userDetail);
        String refreshToken = jwtProvider.createRefreshToken(userDetail);

        LoginTokenDto loginTokenDto = new LoginTokenDto(accessToken, refreshToken);

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

    //refresh토큰 재발급
    public LoginTokenDto refreshToken(String oldRefreshToken, Long memberId) {

        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // redis 갱신된 refresh token 유효성 검증
        if (!redisRepository.hasKey(member.getId())) {
            throw new MemberException(NOT_FOUND_REFRESH_TOKEN);
        }

        // redis에 저장된 토큰과 비교
        if (!redisRepository.getRefreshToken(member.getId()).get("refreshToken").equals(oldRefreshToken)) {
            throw new MemberException(NOT_MATCH_REFRESH_TOKEN);
        }

        UserDetailsImpl userDetail = (UserDetailsImpl) UserDetailsImpl.from(member);

        // accessToken, refreshToken 생성
        String accessToken = jwtProvider.createAccessToken(userDetail);
        String newRefreshToken = jwtProvider.createRefreshToken(userDetail);

        LoginTokenDto loginTokenDto = LoginTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();

        // redis 토큰 정보 저장
        redisRepository.saveToken(userDetail.getId(), newRefreshToken);

        return loginTokenDto;

    }
}
