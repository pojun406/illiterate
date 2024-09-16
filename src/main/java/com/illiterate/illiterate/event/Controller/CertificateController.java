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
@RequestMapping("/public")
public class CertificateController {
    private final CertificateService certificateService;

    @PostMapping(value = "/sendVerificationEmail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BfResponse<CertificateMailResponseDto>> sendCertificateMail(
            @Valid @RequestBody MailCertificateRequestDto mailCertificateRequestDto
    ) {
        CertificateMailResponseDto responseDto = certificateService.sendEmailCertificateNumber(
                mailCertificateRequestDto);

        return ResponseEntity.ok()
                .body(new BfResponse<>(SUCCESS, responseDto));
    }

    @PostMapping(value = "/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BfResponse<?>> verifyMailCertificationNumber(
            @RequestBody VerifyCertificateRequestDto verifyRequestDto
    ) {
        String email = verifyRequestDto.getEmail();
        String verificationCode = verifyRequestDto.getVerificationCode();
        boolean isValid = certificateService.verifyCertificateEmailNumber(email, verificationCode);
        return ResponseEntity.ok()
                .body(new BfResponse<>(SUCCESS, Map.of("isValid", isValid)));
    }
}
