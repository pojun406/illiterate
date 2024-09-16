package com.illiterate.illiterate.common.response;

import com.illiterate.illiterate.common.enums.BaseErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ErrorResponseHandler {
    public ResponseEntity<BfResponse<?>> handleErrorResponse(BaseErrorCode errorCode) {
        ErrorResponse errorResponse = errorCode.getErrorResponse();
        BfResponse<Object> response = new BfResponse<>(errorResponse.getErrorCode(), errorResponse.getErrorMessage());
        return new ResponseEntity<>(response, errorCode.getStatus()); // 상태 코드는 열거형에서 가져옴
    }
}