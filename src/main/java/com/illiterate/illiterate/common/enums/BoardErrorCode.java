package com.illiterate.illiterate.common.enums;

import com.illiterate.illiterate.common.response.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BoardErrorCode implements BaseErrorCode {
    NOT_VALID_TOKEN(400, "토큰이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_WRITER(400, "작성자만 삭제/수정할 수 있습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATED_USERNAME(400, "중복된 username 입니다.", HttpStatus.BAD_REQUEST),
    NOT_MATCHING_INFO(400, "회원을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_MATCHING_PASSWORD(400, "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_USER(400, "사용자가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_WRITING(400, "게시글/댓글이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_IMG(404, "이미지가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN_UPDATE_WRITING(403, "사용자가 맞지 않습니다. 업데이트 실패", HttpStatus.FORBIDDEN),
    FORBIDDEN_DELETE_WRITING(403, "사용자가 맞지 않습니다. 삭제 실패", HttpStatus.FORBIDDEN);

    private final int errorCode;
    private final String errorMessage;
    private final HttpStatus status;

    BoardErrorCode(int errorCode, String message, HttpStatus status) {
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.status = status;
    }

    @Override
    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(this.errorCode, this.errorMessage);
    }
}
