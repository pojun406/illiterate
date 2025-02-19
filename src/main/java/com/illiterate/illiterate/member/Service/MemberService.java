package com.illiterate.illiterate.member.Service;

import com.illiterate.illiterate.common.enums.MemberErrorCode;
import com.illiterate.illiterate.common.repository.RedisRepository;
import com.illiterate.illiterate.common.util.ConvertUtil;
import com.illiterate.illiterate.event.Service.CertificateService;
import com.illiterate.illiterate.event.dto.request.MailCertificateRequestDto;
import com.illiterate.illiterate.event.dto.response.CertificateMailResponseDto;
import com.illiterate.illiterate.member.DTO.request.*;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
import com.illiterate.illiterate.member.DTO.response.MemberInfoDto;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.Repository.MemberRepository;
import com.illiterate.illiterate.member.enums.RolesType;
import com.illiterate.illiterate.member.enums.StatusType;
import com.illiterate.illiterate.member.exception.MemberException;
import com.illiterate.illiterate.security.Service.UserDetailsImpl;
import com.illiterate.illiterate.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.illiterate.illiterate.common.enums.MemberErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisRepository redisRepository;
    private final CertificateService certificateService;
    private final JwtProvider jwtProvider;

    /**
     * 회원 등록
     */
    @Transactional
    public Long joinUser(JoinDto joinDto) {
        // 아이디 중복 체크
        if (memberRepository.existsByUserId(joinDto.getUserid())) {
            System.out.println("중복된 아이디입니다: " + joinDto.getUserid());
            throw new MemberException(DUPLICATED_MEMBER_EMAIL);
        } else if (memberRepository.existsByEmail(joinDto.getEmail())) {
            System.out.println("중복된 이메일입니다: " + joinDto.getEmail());
            throw new MemberException(DUPLICATED_MEMBER_EMAIL);
        }

        Member member = ConvertUtil.toDtoOrEntity(joinDto, Member.class);
        member.setUserId(joinDto.getUserid());
        member.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        member.setEmail(joinDto.getEmail());
        member.setUserName(joinDto.getUsername());

        memberRepository.save(member);

        return member.getIndex();
    }
    public boolean checkId(String userEmail){
        return memberRepository.existsByEmail(userEmail);
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
        String accessToken = jwtProvider.createAccessToken(userDetail);
        String refreshToken = jwtProvider.createRfreshToken(userDetail);

        LoginTokenDto loginTokenDto = LoginTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(userDetail.getId())
                .role(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList().get(0))
                .build();

        // redis 토큰 정보 저장
        redisRepository.saveToken(userDetail.getId(), refreshToken);

        return loginTokenDto;
    }

    // 비밀번호 초기화
    @Transactional
    public void resetPassword(UserDetailsImpl userDetails, Long memberId, MemberPasswordResetRequestDto resetRequestDto) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // 현재 로그인된 유저와 탈퇴하려는 회원이 일치하는지 확인 (관리자는 예외)
        if (!userDetails.getId().equals(memberId)) {
            throw new MemberException(FORBIDDEN_RESET_PASSWORD);
        }

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(resetRequestDto.getNewPassword());

        // 비밀번호 업데이트
        member.setPassword(encodedPassword);

        // 회원 정보 저장
        memberRepository.save(member);
    }

    // 비밀번호 찾기 ( 비로그인중일때 )
    public boolean findPassword(MemberPasswordResetRequestDto resetRequestDto) {

        Member member = new Member();

        try{
            // 회원 조회
            member = memberRepository.findByEmail(resetRequestDto.getEmail())
                    .orElseThrow(() -> new MemberException(FORBIDDEN_RESET_PASSWORD));
        } catch (MemberException e){
            throw new MemberException(FORBIDDEN_DELETE_MEMBER);
        }


        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(resetRequestDto.getNewPassword());

        // 비밀번호 업데이트
        member.setPassword(encodedPassword);

        // 회원 정보 저장
        memberRepository.save(member);

        return true;
    }

    public CertificateMailResponseDto sendCertificationNumber(MailCertificateRequestDto dto) {
        System.out.println("email : " + dto.email());
        Member member = memberRepository.findByEmail(dto.email())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // member가 null인지 확인
        if (member == null) {
            throw new MemberException(NOT_FOUND_MEMBER_ID); // 적절한 예외 처리
        }
        // 이메일 일치 여부 확인
        if (member.getEmail().equals(dto.email())) {
            if(member.getStatus().equals(StatusType.INACTIVE)){
                throw new MemberException(INACTIVE_MEMBER);
            }
            return certificateService.sendEmailCertificateNumber(dto);
        } else {
            throw new MemberException(NOT_FOUND_MEMBER_ID); // 적절한 예외 처리
        }
    }


    public MemberInfoDto getUserInfo(UserDetailsImpl userDetails) {
        System.out.println("userDetail : " + userDetails);
        Member member = memberRepository.findById(userDetails.getId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_EMAIL));

        return MemberInfoDto.builder()
                .email(member.getEmail())
                .name(member.getUserName())
                .userid(member.getUserId())
                .build();
    }

    public String findMemberId(String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));
        return member.getUserId();
    }
/*

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
*/
    /*@Transactional
    public void resetPassword(String token, String newPassword) {
        User member = userRepository.findByResetToken(token)
                .orElseThrow(() -> new MemberException(TOKEN_EXPIRATION));

        member.setPassword(passwordEncoder.encode(newPassword));
        member.setResetToken(null);
        userRepository.save(member);
    }*/

    @Transactional
    public void updateUserInfo(UserDetailsImpl userDetails, MemberUpdateRequestDto userUpdateDto) {

        // 회원 조회
        Member member = memberRepository.findById(userDetails.getId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        log.debug("Member found: {}", member);

        // 이름 업데이트
        member.setUserName(userUpdateDto.getName());

        // 이메일 변경
        member.setEmail(userUpdateDto.getEmail());

        memberRepository.save(member);
        log.debug("Member info updated successfully");
    }


    /**
     * 회원 탈퇴 실제 삭제처리 하지 않고, INACTIVE 처리
     */
    @Transactional
    public void inactiveMember(UserDetailsImpl userDetails, Long memberId) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // 현재 로그인된 유저와 탈퇴하려는 회원이 일치하는지 확인 (관리자는 예외)
        if (member.getRoles() != RolesType.ROLE_ADMIN
                && !userDetails.getId().equals(memberId)) {
            throw new MemberException(FORBIDDEN_DELETE_MEMBER);
        }

        member.setStatus(StatusType.INACTIVE);
    }

    /*public void sendEmailVerification(String email) {
        certificateService.sendEmailCertificateNumber(email);
    }*/

    /**
     * 기존 refresh token으로 신규 access token, refresh token 발급
     */
    @Transactional
    public LoginTokenDto refreshToken(String oldRefreshToken, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // redis 갱신된 refresh token 유효성 검증
        if (!redisRepository.hasKey(member.getIndex())) {
            throw new MemberException(NOT_FOUND_REFRESH_TOKEN);
        }

        // redis에 저장된 토큰과 비교
        if (!redisRepository.getRefreshToken(member.getIndex()).get("refreshToken").equals(oldRefreshToken)) {
            throw new MemberException(NOT_MATCH_REFRESH_TOKEN);
        }

        UserDetailsImpl userDetail = (UserDetailsImpl) UserDetailsImpl.from(member);

        // accessToken, refreshToken 생성
        String accessToken = jwtProvider.createAccessToken(userDetail);
        String newRefreshToken = jwtProvider.createRfreshToken(userDetail);

        LoginTokenDto loginTokenDto = LoginTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();

        // redis 토큰 정보 저장
        redisRepository.saveToken(userDetail.getId(), newRefreshToken);

        return loginTokenDto;

    }
}
