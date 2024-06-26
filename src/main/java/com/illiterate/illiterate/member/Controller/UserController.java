package com.illiterate.illiterate.member.Controller;


import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.DTO.request.*;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
import com.illiterate.illiterate.member.DTO.response.UserInfoDto;
import com.illiterate.illiterate.member.Service.UserService;
import com.illiterate.illiterate.security.JWT.JWTProvider;
import com.illiterate.illiterate.security.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.CREATE;
import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.SUCCESS;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JWTProvider jwtProvider;


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
                        Map.of("userid", userService.joinUser(joinDTO))));
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
        return ResponseEntity.ok(new BfResponse<>(userService.login(loginDto)));
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
            @Valid @RequestBody UserPasswordResetRequestDto resetRequestDto
    ) {
        userService.resetPassword(userDetails, memberId, resetRequestDto);
        System.out.println(ResponseEntity.noContent().build());
        return ResponseEntity.noContent().build();
    }

    /*
    request :
        "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        "userid" : "test"
    response :
        "accessToken": "newAccessToken...",
        "refreshToken": "newRefreshToken...",
        "id": 1
     */
    // refresh토큰을 다시 설정해줌

    @PostMapping("/refresh")
    public ResponseEntity<BfResponse<LoginTokenDto>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(new BfResponse<>(userService.refreshToken(dto.refreshToken(), userDetails.getId())));
    }

    // id 찾기 ( email을 입력하면 id를 찾을 수 있게 로직을 구성 )
    /*
    request :
        "userEmail": "testuser@example.com"
    response :
        "data": "testuser" (user의 id값)
     */
    @PostMapping("/findId")
    public ResponseEntity<BfResponse<?>> findId(@RequestParam String userEmail){
        System.out.println(userService.findMemberId(userEmail));
        return ResponseEntity.ok(new BfResponse<>(userService.findMemberId(userEmail)));
    }

    /*
    request :
        "userId": "testuser"
    response :
        "data": true (boolean)
     */

    // 중복되는 id찾기
    @PostMapping("checkId")
    public ResponseEntity<BfResponse<?>> CheckId(@RequestParam String userId){
        return ResponseEntity.ok(new BfResponse<>(userService.checkId(userId)));
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
    public ResponseEntity<BfResponse<UserInfoDto>> getMemberInfo(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(new BfResponse<>(userService.getUserInfo(userId)));
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
            @Valid @RequestBody UserUpdateRequestDto userUpdateDto
    ) {
        userService.updateUserInfo(userDetails, userId, userUpdateDto);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS));
    }

    /*
    request :
        "userId" : 1 ( Long )
    */
    // 회원 삭제
    @DeleteMapping("/deluser/{userId}")
    public ResponseEntity<BfResponse<?>> inactivateMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId
    ) {
        userService.inactiveMember(userDetails, userId);
        return ResponseEntity.noContent().build();
    }
}