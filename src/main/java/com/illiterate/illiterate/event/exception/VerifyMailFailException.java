package com.illiterate.illiterate.event.exception;


import com.illiterate.illiterate.common.enums.CertificateErrorCode;
import lombok.Getter;

@Getter
public class VerifyMailFailException extends RuntimeException{
    private final CertificateErrorCode errorCode;

    public VerifyMailFailException(CertificateErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
