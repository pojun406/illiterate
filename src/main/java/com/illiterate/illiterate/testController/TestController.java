package com.illiterate.illiterate.testController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/test")
    public String hello() {
        return "테스트입니다.";
    }

    @PostMapping("/api/test/python")
    public String test_py(){
        ProcessBuilder processBuilder = new ProcessBuilder("python", "test.py", "arg1","agr2");
    }
}