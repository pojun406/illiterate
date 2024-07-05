package com.illiterate.illiterate.security.Exception;

import com.illiterate.illiterate.common.enums.BaseErrorCode;
import lombok.Getter;

@Getter
public class CustomSecurityException extends RuntimeException{
    private final BaseErrorCode errorCode;

    public CustomSecurityException(BaseErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}

