package com.illiterate.illiterate.member.DTO.response;

import lombok.Builder;

@Builder
public record UserInfoDto(
        Long id,
        String email,
        String name
) {
}
