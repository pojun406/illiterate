package com.illiterate.illiterate.Controller;


import com.illiterate.illiterate.DTO.JoinDto;
import com.illiterate.illiterate.JWT.JWTUtil;
import com.illiterate.illiterate.Service.JoinService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final JoinService joinService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public UserController(JoinService joinService, AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.joinService = joinService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/join")
    public String registerUser(@RequestBody JoinDto joinDTO) {
        joinService.joinProcess(joinDTO);
        return "ok";
    }

    @PostMapping("/login")
    public void loginUser(@RequestParam String id, @RequestParam String password, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 로그인 요청을 LoginFilter를 통해 처리
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, password));

            // 인증에 성공한 경우 JWT 토큰 생성
            String token = response.getHeader("Authorization").substring(7); // "Bearer " 이후의 토큰 값만 추출

            // 토큰을 클라이언트에게 반환
            response.getWriter().write(token);
        } catch (AuthenticationException e) {
            // 인증에 실패한 경우
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}