package com.illiterate.illiterate.Controller;


import com.illiterate.illiterate.DTO.JoinDto;
import com.illiterate.illiterate.JWT.JWTUtil;
import com.illiterate.illiterate.Service.CustomUserDetailsService;
import com.illiterate.illiterate.Service.JoinService;
import com.illiterate.illiterate.Service.LoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
}