package com.illiterate.illiterate.security.Service;

import com.illiterate.illiterate.common.response.BfResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface ReissueService {
    ResponseEntity<BfResponse<?>> reissue(String refreshToken, Long userId, HttpServletRequest request, HttpServletResponse response);
}
