package com.illiterate.illiterate.security.Controller;

import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.security.DTO.RefreshRequest;
import com.illiterate.illiterate.security.Service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ReissueController {

    private final ReissueService reissueService;

    public ReissueController(ReissueService reissueService) {
        this.reissueService = reissueService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<BfResponse<?>> reissue(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = (String) requestBody.get("refreshToken");
        Long userId = Long.valueOf((String) requestBody.get("userId"));
        return reissueService.reissue(refreshToken, userId, request, response);
    }
}