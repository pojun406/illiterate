package com.illiterate.illiterate;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderExample {
    public static void main(String[] args) {
        String rawPassword = "1234";

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("Encoded Password: " + encodedPassword);
    }
}
