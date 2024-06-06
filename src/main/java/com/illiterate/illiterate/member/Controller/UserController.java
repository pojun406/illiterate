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

    @PostMapping("/join")
    public ResponseEntity<BfResponse<?>> registerUser(@RequestBody JoinDto joinDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE,
                        Map.of("member_id", userService.joinUser(joinDTO))));
    }
    @PostMapping("/login")
    public ResponseEntity<BfResponse<LoginTokenDto>> login(@Valid @RequestBody LoginDto loginDto) {
        System.out.println("res_enti" + ResponseEntity.ok(new BfResponse<>(userService.login(loginDto))));
        System.out.println("userservice.login : " + userService.login(loginDto));
        return ResponseEntity.ok(new BfResponse<>(userService.login(loginDto)));
        //return userService.login(loginDto);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<BfResponse<?>> resetPassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("userId") Long memberId,
            @Valid @RequestBody UserPasswordResetRequestDto resetRequestDto
    ) {
        userService.resetPassword(userDetails, memberId, resetRequestDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public LoginTokenDto refreshAccessToken(
    //public ResponseEntity<BfResponse<LoginTokenDto>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        //return ResponseEntity.ok(new BfResponse<>(userService.refreshToken(dto.refreshToken(), userDetails.getId())));
        return userService.refreshToken(dto.refreshToken(), userDetails.getId());
    }

    @PostMapping("/findid")
    public ResponseEntity<BfResponse<?>> findId(@RequestParam String userEmail){
        return ResponseEntity.ok(new BfResponse<>(userService.findMemberId(userEmail)));
    }

    @PostMapping("checkId")
    public ResponseEntity<BfResponse<?>> CheckId(@RequestParam String userId){
        return ResponseEntity.ok(new BfResponse<>(userService.checkId(userId)));
    }

}