package com.illiterate.illiterate.member.DTO.response;

import lombok.Builder;

@Builder
public class LoginResponseDto {
    private Long id;

    public LoginResponseDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
