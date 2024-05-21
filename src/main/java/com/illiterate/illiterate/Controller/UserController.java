package com.illiterate.illiterate.Controller;


import com.illiterate.illiterate.Entity.User;
import com.illiterate.illiterate.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/join")
    public String registerUser(@RequestBody User user) {
        userService.registerUser(user);
        return "ok";
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        String token = userService.authenticate(email, password);
        if (token != null) {
            return ResponseEntity.ok("로그인 성공: " + token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 자격 증명");
        }
    }
}