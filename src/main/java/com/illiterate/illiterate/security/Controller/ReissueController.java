package com.illiterate.illiterate.security.Controller;

import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.security.Service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReissueController {

    private final ReissueService reissueService;

    public ReissueController(ReissueService reissueService) {
        this.reissueService = reissueService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<BfResponse<?>> reissue(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(new BfResponse<>(reissueService.reissue(request, response)));
    }
}
