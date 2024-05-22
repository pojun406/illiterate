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

    private final JoinService joinService;

    public UserController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/join")
    public String registerUser(@RequestBody JoinDto joinDTO) {
        joinService.joinProcess(joinDTO);
        return "ok";
    }
}