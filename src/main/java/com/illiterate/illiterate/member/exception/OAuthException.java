package com.illiterate.illiterate.member.exception;


import com.backtothefuture.domain.common.enums.OAuthErrorCode;
import lombok.Getter;

@Getter
public class OAuthException extends RuntimeException{
    private final OAuthErrorCode errorCode;

    public OAuthException(OAuthErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
