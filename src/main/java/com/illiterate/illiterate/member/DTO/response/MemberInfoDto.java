package com.illiterate.illiterate.member.DTO.response;

import lombok.Builder;

@Builder
public record MemberInfoDto(
        Long id,
        String email,
        String name
) {
}
