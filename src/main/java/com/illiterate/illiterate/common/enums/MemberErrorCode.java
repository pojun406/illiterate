package com.illiterate.illiterate.common.enums;


import com.illiterate.illiterate.common.response.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode implements BaseErrorCode {
    // 400 BAD_REQUEST
    BAD_REQUEST(400, "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_MEMBER_EMAIL(400, "존재하지 않는 회원입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATED_MEMBER_EMAIL(400, "이미 존재하는 회원 이메일입니다.", HttpStatus.BAD_REQUEST),
    DELETE_MEMBER(400, "탈퇴 또는 삭제된 회원입니다.", HttpStatus.BAD_REQUEST),
    CHECK_ID_OR_PASSWORD(400, "아이디 또는 비밀번호를 확인해주세요.", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCHED(400, "비밀번호와 비밀번호 확인이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_IMAGE_EXTENSION(400, "지원하지 않는 확장자 입니다. jpeg혹은 png 파일을 업로드 해주세요.", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRATION(400, "토큰이 만료됐습니다. 다시 시도해주세요.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_INFO(400, "존재하지 않는 문서입니다.", HttpStatus.BAD_REQUEST),

    // 403 FORBIDDEN
    FORBIDDEN_DELETE_MEMBER(403, "권한이 없습니다. 본인 계정만 탈퇴할 수 있습니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_RESET_PASSWORD(403, "권한이 없습니다. 본인 계정만 비밀번호 변경이 가능합니다.", HttpStatus.FORBIDDEN),

    // 404 NOT_FOUND
    NOT_FOUND_MEMBER_ID(404, "존재하지 않는 회원입니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_REFRESH_TOKEN(404, "refresh token이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOT_MATCH_REFRESH_TOKEN(404, "refresh token이 일치하지 않습니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_BANK(404, "존재하지 않는 은행입니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_TERM_HISTORY(404, "존재하지 않는 약관 이력입니다.", HttpStatus.NOT_FOUND),

    // 500 INTERNAL_SERVER_ERROR
    BUSINESS_VALIDATE_ERROR(500, "사업자등록 진위여부 확인에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BUSINESS_STATUS_ERROR(500, "사업자등록번호 상태조회에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_UPLOAD_FAIL(500, "이미지 업로드에 실패했습니다. 관리자에게 문의해 주세요.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final int errorCode;
    private final String errorMessage;
    private final HttpStatus status;

    MemberErrorCode(int errorCode, String message, HttpStatus status) {
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.status = status;
    }

    @Override
    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(this.errorCode, this.errorMessage);
    }
}
