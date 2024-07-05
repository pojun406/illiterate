package com.illiterate.illiterate.common.response;


import com.illiterate.illiterate.common.enums.GlobalSuccessCode;
import lombok.Getter;

import static com.illiterate.illiterate.common.enums.GlobalSuccessCode.SUCCESS;

@Getter
public class BfResponse<T> {
    private int code;

    private String message;

    private T data;

    public BfResponse(T data) {
        this.code = SUCCESS.getCode();
        this.message = SUCCESS.getMessage();
        this.data = data;
    }

    public BfResponse(GlobalSuccessCode statusCode, T data) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
        this.data = data;
    }

    public BfResponse(GlobalSuccessCode statusCode) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
    }
}
