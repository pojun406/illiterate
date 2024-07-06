package com.illiterate.illiterate.event.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CertificateMailResponseDto {
    private final int mailExpirationSeconds;
    private final String certificationNumber;
}