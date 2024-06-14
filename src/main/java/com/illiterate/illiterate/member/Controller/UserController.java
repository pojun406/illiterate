package com.illiterate.illiterate.member.Controller;


import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.DTO.request.*;
import com.illiterate.illiterate.member.DTO.response.LoginResponseDto;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
import com.illiterate.illiterate.member.DTO.response.UserInfoDto;
import com.illiterate.illiterate.member.Service.UserService;
import com.illiterate.illiterate.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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

    /*
    "userid": "testuser",
    "username": "Test User",
    "password": "password123",
    "email": "testuser@example.com"
     */
    //회원가입
    @PostMapping("/join")
    public ResponseEntity<BfResponse<?>> registerUser(@RequestBody JoinDto joinDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE,
                        Map.of("userid", userService.joinUser(joinDTO))));
    }
    /*
         "userid": "testuser",
        "password": "password123"
     */
    //로그인
    @PostMapping("/login")
    public ResponseEntity<BfResponse<LoginResponseDto>> login(@Valid @RequestBody LoginDto loginDto) {
        LoginTokenDto loginTokenDto = userService.login(loginDto);

        // 토큰을 헤더에 삽입
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Token", loginTokenDto.accessToken());
        headers.set("Refresh-Token", loginTokenDto.refreshToken());

        // id만 json에 입력
        LoginResponseDto loginResponseDto = new LoginResponseDto(loginTokenDto.id());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new BfResponse<>(loginResponseDto));
    }

    /*
        "newPassword": "newpassword123"
     */
    //리셋 패스워드(패스워드 리셋을 위한 페이지)
    @PatchMapping("/{userId}/password")
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
        "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        "userid" : "test"
     */
    // refresh토큰을 다시 설정해줌

    @PostMapping("/refresh")
    //public LoginTokenDto refreshAccessToken(
    public ResponseEntity<BfResponse<LoginTokenDto>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println("dto : " + dto);
        System.out.println("user detail : " + userDetails);
        return ResponseEntity.ok(new BfResponse<>(userService.refreshToken(dto.refreshToken(), userDetails.getId())));
        //return userService.refreshToken(dto.refreshToken(), userDetails.getId());
    }

    // id 찾기 ( email을 입력하면 id를 찾을 수 있게 로직을 구성 )
    /*
    "userEmail"
     */
    @PostMapping("/findId")
    public ResponseEntity<BfResponse<?>> findId(@RequestParam String userEmail){
        System.out.println(userService.findMemberId(userEmail));
        return ResponseEntity.ok(new BfResponse<>(userService.findMemberId(userEmail)));
    }

    /*
    "userId": "testuser"
     */

    // 중복되는 id찾기
    @PostMapping("checkId")
    public ResponseEntity<BfResponse<?>> CheckId(@RequestParam String userId){
        return ResponseEntity.ok(new BfResponse<>(userService.checkId(userId)));
    }

    /*
    Request X
     */
    // 회원정보 조회
    @GetMapping("/userinfo")
    public ResponseEntity<BfResponse<UserInfoDto>> getMemberInfo(
            @PathVariable Long memberId
    ) {
        return ResponseEntity.ok(new BfResponse<>(userService.getUserInfo(memberId)));
    }


    /*
    "name": "Updated User",
    "email": "updateduser@example.com"
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<BfResponse<?>> updateMemberInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequestDto userUpdateDto
    ) {
        userService.updateUserInfo(userDetails, userId, userUpdateDto);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS));
    }

    //Request X
    // 회원 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<BfResponse<?>> inactivateMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("userId") Long userId
    ) {
        userService.inactiveMember(userDetails, userId);
        return ResponseEntity.noContent().build();
    }
}