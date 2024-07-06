package com.illiterate.illiterate.member.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindIdRequestDto {
    private String userEmail;
    private String userId;
}

