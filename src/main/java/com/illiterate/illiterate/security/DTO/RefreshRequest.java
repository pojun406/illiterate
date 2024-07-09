package com.illiterate.illiterate.security.DTO;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
    private Long userId;
}
