package com.illiterate.illiterate.Controller;


import com.illiterate.illiterate.DTO.JoinDto;
import com.illiterate.illiterate.JWT.JWTUtil;
import com.illiterate.illiterate.Service.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    private final JoinService joinService;

    public UserController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/join")
    public String registerUser(JoinDto joinDTO) {
        joinService.joinProcess(joinDTO);
        return "ok";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            // 실제 인증 수행
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // JWT 토큰 생성
            String token = jwtUtil.createJwt(username, 3600000L); // 수명 1시간
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            System.out.println(token + " <= 토큰값");
            System.out.println(authentication.isAuthenticated());

            return response;
        } catch (AuthenticationException e) {
            throw new RuntimeException("자격증명 오류");
        }
    }

}