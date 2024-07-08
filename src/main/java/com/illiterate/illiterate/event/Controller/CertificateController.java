package com.illiterate.illiterate.event.Controller;

import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.event.Service.CertificateService;
import com.illiterate.illiterate.event.dto.request.MailCertificateRequestDto;
import com.illiterate.illiterate.event.dto.request.VerifyCertificateRequestDto;
import com.illiterate.illiterate.event.dto.response.CertificateMailResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.SUCCESS;

@Controller
@RequiredArgsConstructor
public class CertificateController {
    private final CertificateService certificateService;

    @PostMapping(value = "/sendVerificationEmail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BfResponse<CertificateMailResponseDto>> sendCertificateMail(
            @Valid @RequestBody MailCertificateRequestDto mailCertificateRequestDto) {
        CertificateMailResponseDto responseDto = certificateService.sendEmailCertificateNumber(mailCertificateRequestDto.email());
        return ResponseEntity.ok().body(new BfResponse<>(SUCCESS, responseDto));
    }

    @GetMapping(value = "/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BfResponse<?>> verifyMailCertificationNumber(@Valid @RequestBody VerifyCertificateRequestDto verifyRequestDto) {
        boolean isValid = certificateService.verifyCertificateEmailNumber(verifyRequestDto.getEmail(), verifyRequestDto.getCertificationNumber());
        if (isValid) {
            // 인증 성공 시 JWT 생성 및 반환
            String token = certificateService.generateToken(verifyRequestDto.getEmail());
            return ResponseEntity.ok().body(new BfResponse<>(SUCCESS, Map.of("token", token)));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BfResponse<>(null, "Invalid certification number"));
        }
    }
}
