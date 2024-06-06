package com.illiterate.illiterate.member.Controller;


import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.DTO.request.JoinDto;
import com.illiterate.illiterate.member.DTO.request.LoginDto;
import com.illiterate.illiterate.member.DTO.request.RefreshTokenRequestDto;
import com.illiterate.illiterate.member.DTO.request.UserPasswordResetRequestDto;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
import com.illiterate.illiterate.member.Service.UserService;
import com.illiterate.illiterate.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.CREATE;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/join")
    public ResponseEntity<BfResponse<?>> registerUser(@RequestBody JoinDto joinDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE,
                        Map.of("member_id", userService.joinUser(joinDTO))));
    }
    //로그인
    @PostMapping("/login")
    public ResponseEntity<BfResponse<LoginTokenDto>> login(@Valid @RequestBody LoginDto loginDto) {

        return ResponseEntity.ok(new BfResponse<>(userService.login(loginDto)));
        //return userService.login(loginDto);
    }

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

    // refresh토큰을 다시 설정해줌
    @PostMapping("/refresh")
    //public LoginTokenDto refreshAccessToken(
    public ResponseEntity<BfResponse<LoginTokenDto>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(new BfResponse<>(userService.refreshToken(dto.refreshToken(), userDetails.getId())));
        //return userService.refreshToken(dto.refreshToken(), userDetails.getId());
    }

    // id 찾기 ( email을 입력하면 id를 찾을 수 있게 로직을 구성 )
    @PostMapping("/findId")
    public ResponseEntity<BfResponse<?>> findId(@RequestParam String userEmail){
        System.out.println(userService.findMemberId(userEmail));
        return ResponseEntity.ok(new BfResponse<>(userService.findMemberId(userEmail)));
    }

    // 중복되는 id찾기
    @PostMapping("checkId")
    public ResponseEntity<BfResponse<?>> CheckId(@RequestParam String userId){
        return ResponseEntity.ok(new BfResponse<>(userService.checkId(userId)));
    }

}