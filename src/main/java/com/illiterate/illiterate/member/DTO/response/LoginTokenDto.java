package com.illiterate.illiterate.member.DTO.response;

import lombok.Builder;

@Builder
public record LoginTokenDto(
        String accessToken,
        String refreshToken
) {
}
