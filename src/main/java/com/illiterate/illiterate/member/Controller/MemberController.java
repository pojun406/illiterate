package com.illiterate.illiterate.member.Controller;


import com.illiterate.illiterate.common.enums.GlobalErrorCode;
import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.common.response.ErrorResponseHandler;
import com.illiterate.illiterate.event.dto.request.MailCertificateRequestDto;
import com.illiterate.illiterate.member.DTO.request.*;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
import com.illiterate.illiterate.member.DTO.response.MemberInfoDto;
import com.illiterate.illiterate.member.Service.MemberService;
import com.illiterate.illiterate.member.exception.MemberException;
import com.illiterate.illiterate.security.Service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.CREATE;
import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.SUCCESS;
import static com.illiterate.illiterate.common.enums.MemberErrorCode.NOT_FOUND_MEMBER_EMAIL;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final ErrorResponseHandler errorResponseHandler;


    //회원가입
    @PostMapping("/public/join")
    public ResponseEntity<BfResponse<?>> registerUser(@RequestBody JoinDto joinDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE,
                        Map.of("userid", memberService.joinUser(joinDTO))));
    }

    //로그인
    @PostMapping("/public/login")
    public ResponseEntity<BfResponse<?>> login(@Valid @RequestBody LoginDto loginDto) {
        try{
            return ResponseEntity.ok(new BfResponse<>(memberService.login(loginDto)));
        } catch (MemberException e){
            return errorResponseHandler.handleErrorResponse(e.getErrorCode());
        } catch (Exception e){
            return errorResponseHandler.handleErrorResponse(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }

        //return userService.login(loginDto);
    }

    //리셋 패스워드(패스워드 리셋을 위한 페이지)
    @PostMapping("/user/resetPassword/{userId}")
    public ResponseEntity<BfResponse<?>> resetPassword(
            @AuthenticationPrincipal UserDetailsImpl userDetail,
            @PathVariable("userId") Long memberId,
            @Valid @RequestBody MemberPasswordResetRequestDto resetRequestDto
    ) {

        memberService.resetPassword(userDetail, memberId, resetRequestDto);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS, "비밀번호 초기화 완료"));
    }

    //리셋 패스워드(패스워드 리셋을 위한 페이지)
    @PostMapping("/public/findPassword")
    public ResponseEntity<BfResponse<?>> findPassword(
            @Valid @RequestBody MemberPasswordResetRequestDto resetRequestDto
    ) {

        boolean isValid = memberService.findPassword(resetRequestDto);

        if (!isValid) {
            return ResponseEntity.status(400).body(new BfResponse<>(null, "Invalid certification number"));
        }

        return ResponseEntity.ok(new BfResponse<>(SUCCESS, "비밀번호 초기화 완료"));
    }

    //이메일 인증
    @PostMapping("/public/CertificationNumber")
    public ResponseEntity<BfResponse<?>> getCertificationNumber(@RequestBody MailCertificateRequestDto find){
        try {
            return ResponseEntity.ok(new BfResponse<>(SUCCESS, memberService.sendCertificationNumber(find)));
        } catch (MemberException e){
            return errorResponseHandler.handleErrorResponse(e.getErrorCode());
        }
    }

    @PostMapping("/public/findId")
    public ResponseEntity<BfResponse<?>> findId(
            @RequestBody FindIdRequestDto request){
        try{
            return ResponseEntity.ok(new BfResponse<>(memberService.findMemberId(request.getUserEmail())));
        } catch (MemberException e){
            return errorResponseHandler.handleErrorResponse(e.getErrorCode());
        }
    }

    @PostMapping("/public/checkId")
    public ResponseEntity<BfResponse<?>> checkId(@RequestBody FindIdRequestDto dto) {
        boolean isAvailable = memberService.checkId(dto.getUserEmail());
        return ResponseEntity.ok(new BfResponse<>(isAvailable));
    }

    // 회원정보 조회
    @PostMapping("/user/userinfo")
    public ResponseEntity<BfResponse<MemberInfoDto>> getMemberInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetail
    ) {
        return ResponseEntity.ok(new BfResponse<>(memberService.getUserInfo(userDetail)));
    }

    @PostMapping("/user/userUpdate")
    public ResponseEntity<BfResponse<?>> updateMemberInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetail,
            @Valid @RequestBody MemberUpdateRequestDto userUpdateDto
    ) {
        memberService.updateUserInfo(userDetail, userUpdateDto);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS));
    }

    // 회원 삭제
    @PostMapping("/user/deluser/{userIndex}")
    public ResponseEntity<BfResponse<?>> inactivateMember(
            @AuthenticationPrincipal UserDetailsImpl userDetail, @PathVariable("userIndex") Long userIndex

    ) {
        memberService.inactiveMember(userDetail, userIndex);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<BfResponse<LoginTokenDto>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(new BfResponse<>(memberService.refreshToken(dto.refreshToken(), userDetails.getId())));
    }
}