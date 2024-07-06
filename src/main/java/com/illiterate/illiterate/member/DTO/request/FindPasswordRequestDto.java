package com.illiterate.illiterate.member.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindPasswordRequestDto {
    private String userId;
    private String email;
    private String verificationCode;
}
