package com.illiterate.illiterate.common.response;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ErrorResponse {
    private final int errorCode;
    private final String errorMessage;
    private final Map<String, String> validation = new HashMap<>(); // 검증시 에러가 발생한 필드 이름, 메시지

    public ErrorResponse(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public void addValidation(String field, String message) {
        validation.put(field, message);
    }
}
