package com.illiterate.illiterate.event.dto.response;

import lombok.Builder;

@Builder
public record CertificateMailResponseDto(
        int mailExpirationSeconds,

        String certificationNumber
) {
}
