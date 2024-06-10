package com.illiterate.illiterate.event.Controller;

import com.illiterate.illiterate.common.response.BfResponse;
import com.illiterate.illiterate.event.dto.request.MailCertificateRequestDto;
import com.illiterate.illiterate.event.dto.response.CertificateMailResponseDto;
import com.illiterate.illiterate.event.Service.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.SUCCESS;
@Controller
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificationService;

    // request : "email"
    @PostMapping(value = "/email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BfResponse<CertificateMailResponseDto>> sendCertificateMail(
            @Valid @RequestBody MailCertificateRequestDto mailCertificateRequestDto
    ) {
        CertificateMailResponseDto responseDto = certificationService.sendEmailCertificateNumber(
                mailCertificateRequestDto);

        return ResponseEntity.ok()
                .body(new BfResponse<>(SUCCESS, responseDto));
    }

    // request : /email?email=example@example.com&certificationNumber=123456
    @GetMapping(value = "/email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BfResponse<?>> verifyMailCertificationNumber(@RequestParam String email, @RequestParam String certificationNumber
    ) {
        boolean isValid = certificationService.verifyCertificateEmailNumber(email, certificationNumber);
        return ResponseEntity.ok()
                .body(new BfResponse<>(SUCCESS, Map.of("isValid", isValid)));
    }

}
