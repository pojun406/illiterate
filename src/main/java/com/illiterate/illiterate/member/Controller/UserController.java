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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public String registerUser(@RequestBody JoinDto joinDTO) {
        userService.joinUser(joinDTO);
        return "ok";
    }
    @PostMapping("/login")
    public ResponseEntity<BfResponse<LoginTokenDto>> login(@Valid @RequestBody LoginDto loginDto) {
        System.out.println("res_enti" + ResponseEntity.ok(new BfResponse<>(userService.login(loginDto))));
        System.out.println("userservice.login : " + userService.login(loginDto));
        return ResponseEntity.ok(new BfResponse<>(userService.login(loginDto)));
        //return userService.login(loginDto);
    }

    //id 중복 확인
    @PostMapping("/useridCheck")
    public String checkId(@RequestParam String checkId){
        if(userService.checkId(checkId)) {
            return "ok";
        }else{
            return "no";
        }
    }
    @PostMapping("/send-reset-password-link")
    public String sendResetPasswordLink(@RequestParam String id, @RequestParam String name) {
        userService.sendPasswordResetLink(id, name);
        return "email_send";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        userService.resetPassword(token, newPassword);
        return "ok";
    }

    @PostMapping("/refresh")
    public ResponseEntity<BfResponse<LoginTokenDto>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(new BfResponse<>(userService.refreshToken(dto.refreshToken(), userDetails.getId())));
    }

    @PostMapping("/findid")
    public String findId(@RequestParam String userEmail){
        String userId = userService.findMemberId(userEmail);
        return "userid = " + userId;
    }

}