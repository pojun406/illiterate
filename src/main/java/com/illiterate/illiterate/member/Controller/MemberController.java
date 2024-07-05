package com.illiterate.illiterate.member.Controller;


import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.DTO.request.*;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
import com.illiterate.illiterate.member.DTO.response.MemberInfoDto;
import com.illiterate.illiterate.member.Service.MemberService;
import com.illiterate.illiterate.security.Service.UserDetailsImpl;
import com.illiterate.illiterate.security.Util.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.CREATE;
import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.SUCCESS;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;


    /*
    request :
    "userid": "testuser",
    "username": "Test User",
    "password": "password123",
    "email": "testuser@example.com"

    response :
    "status": "CREATE",
    "data": {
        "userid": "testuser"
    }
     */
    //회원가입
    @PostMapping("/join")
    public ResponseEntity<BfResponse<?>> registerUser(@RequestBody JoinDto joinDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE,
                        Map.of("userid", memberService.joinUser(joinDTO))));
    }
    /*
    request :
        "userid": "testuser",
        "password": "password123"
    response :
        "status": "SUCCESS",
        "data": {
            "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "id": 1
    }
     */
    //로그인
    @PostMapping("/login")
    public ResponseEntity<BfResponse<LoginTokenDto>> login(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(new BfResponse<>(memberService.login(loginDto)));
        //return userService.login(loginDto);
    }

    /*
    {userId}는 유저번호 로컬에서 받아서 넘기는걸로 일단 로직 구성함
        "newPassword": "newpassword123"
     */
    //리셋 패스워드(패스워드 리셋을 위한 페이지)
    @PostMapping("/{userId}/password")
    public ResponseEntity<BfResponse<?>> resetPassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("userId") Long memberId,
            @Valid @RequestBody MemberPasswordResetRequestDto resetRequestDto
    ) {
        memberService.resetPassword(userDetails, memberId, resetRequestDto);
        System.out.println(ResponseEntity.noContent().build());
        return ResponseEntity.noContent().build();
    }

    // id 찾기 ( email을 입력하면 id를 찾을 수 있게 로직을 구성 )
    /*
    request :
        "userEmail": "testuser@example.com"
    response :
        "data": "testuser" (user의 id값)
     */
    @PostMapping("/findId")
    public ResponseEntity<BfResponse<?>> findId(
            @RequestBody String userEmail){
        System.out.println(memberService.findMemberId(userEmail));
        return ResponseEntity.ok(new BfResponse<>(memberService.findMemberId(userEmail)));
    }

    /*
    request :
        "userId": "testuser"
    response :
        "data": true (boolean)
     */

    // 중복되는 id찾기
    @PostMapping("checkId")
    public ResponseEntity<BfResponse<?>> checkId(@RequestBody String userId){
        return ResponseEntity.ok(new BfResponse<>(memberService.checkId(userId)));
    }

    /*
    request :
        "userId" : 1 ( Long )
    response :
        "id": 1,
        "email": "testuser@example.com",
        "name": "Test User"
     */
    // 회원정보 조회
    @PostMapping("/userinfo/{userId}")
    public ResponseEntity<BfResponse<MemberInfoDto>> getMemberInfo(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(new BfResponse<>(memberService.getUserInfo(userId)));
    }


    /*
    request :
        "userId" : 1 ( Long )
        "name": "Updated User",
        "email": "updateduser@example.com"
    response :
    X
     */
    @PostMapping("/userUpdate/{userId}")
    public ResponseEntity<BfResponse<?>> updateMemberInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId,
            @Valid @RequestBody MemberUpdateRequestDto userUpdateDto
    ) {
        memberService.updateUserInfo(userDetails, userId, userUpdateDto);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS));
    }

    /*
    request :
        "userId" : 1 ( Long )
    */
    // 회원 삭제
    @PostMapping("/deluser/{userId}")
    public ResponseEntity<BfResponse<?>> inactivateMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId
    ) {
        memberService.inactiveMember(userDetails, userId);
        return ResponseEntity.noContent().build();
    }
}