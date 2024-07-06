package com.illiterate.illiterate.member.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateRequestDto{
    private String name; // 닉네임
    private String email;
}
