package com.illiterate.illiterate.member.Service;

import com.illiterate.illiterate.common.repository.RedisRepository;
import com.illiterate.illiterate.common.util.ConvertUtil;
import com.illiterate.illiterate.member.DTO.request.JoinDto;
import com.illiterate.illiterate.member.DTO.request.LoginDto;
import com.illiterate.illiterate.member.DTO.request.MemberPasswordResetRequestDto;
import com.illiterate.illiterate.member.DTO.request.MemberUpdateRequestDto;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
import com.illiterate.illiterate.member.DTO.response.MemberInfoDto;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.Repository.MemberRepository;
import com.illiterate.illiterate.member.enums.RolesType;
import com.illiterate.illiterate.member.exception.MemberException;
import com.illiterate.illiterate.security.Service.UserDetailsImpl;
import com.illiterate.illiterate.security.Util.JWTUtil;
import com.illiterate.illiterate.security.Util.TokenExpirationTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static com.illiterate.illiterate.common.enums.GlobalErrorCode.CHECK_USER;
import static com.illiterate.illiterate.common.enums.MemberErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisRepository redisRepository;
    private final JavaMailSender mailSender;
    private final JWTUtil jwtUtil;


    /**
     * 회원 등록
     */
    @Transactional
    public Long joinUser(JoinDto joinDto) {
        // 아이디 중복 체크
        if (memberRepository.existsByUserid(joinDto.getUserid())) {
            System.out.println("중복된 아이디입니다: " + joinDto.getUserid());
            throw new MemberException(DUPLICATED_MEMBER_EMAIL);
        } else if (memberRepository.existsByEmail(joinDto.getEmail())) {
            System.out.println("중복된 이메일입니다: " + joinDto.getEmail());
            throw new MemberException(DUPLICATED_MEMBER_PHONE_NUMBER);
        }

        Member member = ConvertUtil.toDtoOrEntity(joinDto, Member.class);
        member.setUserid(joinDto.getUserid());
        member.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        member.setEmail(joinDto.getEmail());
        member.setUsername(joinDto.getUsername());

        Long id = memberRepository.save(member).getId();

        return id;
    }
    public boolean checkId(String userid){
        return memberRepository.existsByUserid(userid);
    }

    public LoginTokenDto login(LoginDto memberLoginDto) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                memberLoginDto.getUserid(),
                memberLoginDto.getPassword()
        );

        Authentication authenticated = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticated);

        UserDetailsImpl userDetail = (UserDetailsImpl) authenticated.getPrincipal();

        // accessToken, refreshToken 생성
        String accessToken = jwtUtil.createJwt("access", userDetail.getUsername(),
                Collections.singletonList(userDetail.getAuthorities().toString()), TokenExpirationTime.ACCESS_TIME);
        String refreshToken = jwtUtil.createJwt("refresh", userDetail.getUsername(),
                Collections.singletonList(userDetail.getAuthorities().toString()), TokenExpirationTime.REFRESH_TIME);

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
    public void resetPassword(UserDetailsImpl userDetails, Long memberId, MemberPasswordResetRequestDto resetRequestDto) {
        // 본인 or 관리자 권한 확인
        if (!userDetails.getAuthorities().contains(RolesType.ADMIN)
                && !userDetails.getId().equals(memberId)) {
            throw new MemberException(FORBIDDEN_RESET_PASSWORD);
        }

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        member.resetPassword(passwordEncoder.encode(resetRequestDto.newPassword()));
    }

    public MemberInfoDto getUserInfo(Long userId) {
        // 회원 조회
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        return MemberInfoDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getUsername())
                .build();
    }

    public String findMemberId(String email) {
        return memberRepository.findByEmail(email)
                .map(Member::getUserid)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_EMAIL));
    }

    @Transactional
    public void sendPasswordResetLink(String id, String name) {
        Member member = memberRepository.findByUseridAndUsername(id, name)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_EMAIL));

        String resetToken = generateResetToken();
        //member.setResetToken(resetToken);
        memberRepository.save(member);

        String resetUrl = "http://yourdomain.com/reset-password?token=" + resetToken; // 도메인 설정해줘야함

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(member.getEmail());
        message.setSubject("비밀번호 재설정");
        message.setText("비밀번호 재설정을 위해 아래 링크를 눌러주세요\n" + resetUrl);

        mailSender.send(message);
    }

    /*@Transactional
    public void resetPassword(String token, String newPassword) {
        User member = userRepository.findByResetToken(token)
                .orElseThrow(() -> new MemberException(TOKEN_EXPIRATION));

        member.setPassword(passwordEncoder.encode(newPassword));
        member.setResetToken(null);
        userRepository.save(member);
    }*/

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    public void updateUserInfo(UserDetailsImpl userDetails, Long memberId, MemberUpdateRequestDto userUpdateDto) {
        // 현재 로그인된 유저와 수정하려는 회원이 일치하는지 확인
        if (!userDetails.getId().equals(memberId)) {
            throw new MemberException(BAD_REQUEST);
        }

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // 이름 업데이트
        Optional.ofNullable(userUpdateDto.name()).ifPresent(member::updateName);

        // 이메일 변경
        Optional.ofNullable(userUpdateDto.email()).ifPresent(member::updateEmail);

        memberRepository.save(member);
    }

    public void inactiveMember(UserDetailsImpl userDetails, Long memberId) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // 현재 로그인된 유저와 탈퇴하려는 회원이 일치하는지 확인 (관리자는 예외)
        if (member.getRoles() != RolesType.ADMIN
                && !userDetails.getId().equals(memberId)) {
            throw new MemberException(FORBIDDEN_DELETE_MEMBER);
        }

        member.inactivateUser();
    }
}