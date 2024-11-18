package com.illiterate.illiterate.member.DTO.response;

import lombok.Builder;

@Builder
public record MemberInfoDto(
        String email,
        String name,
        String userid
) {
}
