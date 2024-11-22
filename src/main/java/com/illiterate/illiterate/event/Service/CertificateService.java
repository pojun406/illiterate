// CertificateService.java
package com.illiterate.illiterate.event.Service;

import com.illiterate.illiterate.common.enums.CertificateErrorCode;
import com.illiterate.illiterate.common.repository.RedisRepository;
import com.illiterate.illiterate.event.dto.request.MailCertificateRequestDto;
import com.illiterate.illiterate.event.dto.response.CertificateMailResponseDto;
import com.illiterate.illiterate.event.exception.CertificateException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;

import static com.illiterate.illiterate.common.enums.CertificateErrorCode.INVALID_CERTIFCATE_NUMBER;
import static com.illiterate.illiterate.common.enums.CertificateErrorCode.INVALID_PHONE_NUMBER;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificateService {
    private final RedisRepository redisRepository;
    private final MailService mailService;

    private boolean validateCertificationEmailNumber(String email, String certificationNumber) {
        // Redis에서 이메일로 저장된 인증번호 가져오기
        String storedCertificationNumber = redisRepository.getCertificationEmailNumber(email);

        // Redis에 값이 없거나 가져온 값이 null인 경우 처리
        if (storedCertificationNumber == null) {
            log.error("인증번호가 Redis에 존재하지 않거나 이미 만료되었습니다.");
            throw new CertificateException(INVALID_CERTIFCATE_NUMBER);
        } else if (storedCertificationNumber.isEmpty()) {
            throw new CertificateException(INVALID_PHONE_NUMBER);
        }

        // 입력된 인증번호와 Redis에 저장된 인증번호를 비교할 때 trim()을 적용하여 공백 문제를 해결
        return storedCertificationNumber.trim().equals(certificationNumber.trim());
    }

    public CertificateMailResponseDto sendEmailCertificateNumber(MailCertificateRequestDto mailCertificateRequestDto) {
        String email = mailCertificateRequestDto.email();

        // 랜덤 번호 발급
        String randomNumber = createRandomNum(6); // 6자리 임의의 숫자 생성

        // 메일 내용 설정
        HashMap<String, String> content = getCertificationMailContent(randomNumber);

        // 메일 전송 (비동기)
        try {
            mailService.sendMail(email, content);
        } catch (MessagingException e) {
            throw new CertificateException(CertificateErrorCode.MAIL_SEND_ERROR);
        }

        // 임시 발급 번호 redis에 저장
        redisRepository.saveCertificationEmailNumber(email, randomNumber);

        CertificateMailResponseDto response = CertificateMailResponseDto.builder()
                .mailExpirationSeconds(redisRepository.getMailExp())
                .certificationNumber(randomNumber)
                .build();

        return response;
    }

    public boolean verifyCertificateEmailNumber(String email, String certificationNumber) {
        // 유효 번호 검증
        return validateCertificationEmailNumber(email, certificationNumber);
    }

    public HashMap<String, String> getCertificationMailContent(String certificateNumber) {
        return new HashMap<>() {{
            put("subject", "메일 제목");
            put("text", "인증코드 = " + certificateNumber);
        }};
    }

    private static String createRandomNum(int length) { // length는 자릿수

        Random random = new Random(System.currentTimeMillis()); // 시드 설정

        return String.valueOf(
                random.nextInt(9 * (int) Math.pow(10, length - 1)) + (int) Math.pow(10, length - 1));
    }
}
