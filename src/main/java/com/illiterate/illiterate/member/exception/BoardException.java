package com.illiterate.illiterate.member.exception;

import com.illiterate.illiterate.common.enums.BoardErrorCode;
import com.illiterate.illiterate.common.enums.MemberErrorCode;
import lombok.Getter;

@Getter
public class BoardException extends RuntimeException {
    private final BoardErrorCode errorCode;

    public BoardException(BoardErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
