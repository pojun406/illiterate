package com.illiterate.illiterate.member.DTO.request;

public record MemberUpdateRequestDto(
        String name,                            // 닉네임

        String email
) {
}
