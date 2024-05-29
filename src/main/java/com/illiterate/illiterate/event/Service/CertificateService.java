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

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificateService {
    private final RedisRepository redisRepository;

    private final MailService mailService;

    private boolean validateCertificationEmailNumber(String email, String certificationNumber) {
        // 해당 key의 value 값이 존재 하는지 + 인증 번호가 일치 하는지
        return (redisRepository.hashEmailKey(email) && redisRepository.getCertificationEmailNumber(
                email).equals(certificationNumber));
    }

    public static String createRandomNum(int length) { // length는 자릿수

        Random random = new Random(System.currentTimeMillis()); // 시드 설정

        return String.valueOf(
                random.nextInt(9 * (int) Math.pow(10, length - 1)) + (int) Math.pow(10, length - 1));
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
}
