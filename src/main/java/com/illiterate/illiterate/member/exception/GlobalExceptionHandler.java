package com.illiterate.illiterate.member.exception;


import com.illiterate.illiterate.common.enums.BaseErrorCode;
import com.illiterate.illiterate.common.enums.MemberErrorCode;
import com.illiterate.illiterate.common.response.ErrorResponse;
import com.illiterate.illiterate.security.exception.CustomSecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;

import static com.illiterate.illiterate.common.enums.GlobalErrorCode.INTERNAL_SERVER_ERROR;
import static com.illiterate.illiterate.common.enums.GlobalErrorCode.VALIDATION_FAILED;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
		log.error(">>>>> Internal Server Error : {}", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR.getErrorResponse());
	}

	/**
	 * Member RunTime Handler
	 */
	@ExceptionHandler(MemberException.class)
	protected ResponseEntity<ErrorResponse> handleMemberException(MemberException ex) {
		log.warn(">>>>> MemberException : {}", ex);
		MemberErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity.status(errorCode.getStatus()).body(errorCode.getErrorResponse());
	}

	/**
	 * Security 체크 관련
	 */
	@ExceptionHandler(CustomSecurityException.class)
	protected ResponseEntity<ErrorResponse> handleSecurityException(CustomSecurityException ex) {
		log.warn(">>>>> SecurityException : {}", ex);
		BaseErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity.status(errorCode.getStatus()).body(errorCode.getErrorResponse());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	protected ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex){
		log.error(">>>>> ConstraintViolationException : {}", ex);
		ErrorResponse errorResponse = VALIDATION_FAILED.getErrorResponse();
		// 어떤 필드에서 검증 에러가 발생했는지 반환
		ex.getConstraintViolations().stream()
			.forEach(constraintViolation -> {
				errorResponse.addValidation(constraintViolation.getPropertyPath().toString(),
					constraintViolation.getMessage());
			});

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.error(">>>>> MethodArgumentNotValidException : {}", ex);
		ErrorResponse errorResponse = VALIDATION_FAILED.getErrorResponse();
		ex.getBindingResult().getFieldErrors().forEach(fieldError ->{
			String field = fieldError.getField();
			String message = fieldError.getDefaultMessage();
			errorResponse.addValidation(field, message);
		});

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
}
