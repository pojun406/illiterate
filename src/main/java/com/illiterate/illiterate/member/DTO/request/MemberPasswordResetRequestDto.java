package com.illiterate.illiterate.member.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class MemberPasswordResetRequestDto {
    private String email;
    private String newPassword;
    private String certificationNumber;
}
