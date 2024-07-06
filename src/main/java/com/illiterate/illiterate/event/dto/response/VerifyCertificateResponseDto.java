package com.illiterate.illiterate.event.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerifyCertificateResponseDto {
    private final boolean isValid;
}