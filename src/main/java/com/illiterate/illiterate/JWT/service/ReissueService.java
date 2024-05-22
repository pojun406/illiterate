package com.illiterate.illiterate.JWT.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface ReissueService {
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response);
}
