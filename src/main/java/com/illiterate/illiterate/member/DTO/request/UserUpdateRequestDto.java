package com.illiterate.illiterate.member.DTO.request;

import jakarta.validation.constraints.Pattern;
import java.util.List;

public record UserUpdateRequestDto(
        String name,                            // 닉네임

        String email
) {
}
