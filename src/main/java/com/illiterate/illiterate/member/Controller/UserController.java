package com.illiterate.illiterate.member.Controller;


import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.member.DTO.request.JoinDto;
import com.illiterate.illiterate.member.DTO.request.LoginDto;
import com.illiterate.illiterate.member.DTO.response.LoginTokenDto;
import com.illiterate.illiterate.member.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
        return ResponseEntity.ok(new BfResponse<>(userService.login(loginDto)));
    }
}