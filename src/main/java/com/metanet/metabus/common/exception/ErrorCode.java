package com.metanet.metabus.common.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorCode {
    INVALID_PASSWORD(UNAUTHORIZED, "잘못된 패스워드입니다."),
    MEMBER_NOT_LOGGED_IN(UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_FILE_FORMAT(UNAUTHORIZED, "올바르지 않은 파일 형식입니다"),
    INVALID_PERMISSION(FORBIDDEN, "권한이 없습니다."),
    MEMBER_NOT_FOUND(NOT_FOUND, "해당하는 유저를 찾을 수 없습니다."),
    BUS_NOT_FOUND(NOT_FOUND, "해당하는 버스를 찾을 수 없습니다."),
    EMAIL_NOT_FOUND(NOT_FOUND, "해당하는 이메일을 찾을 수 없습니다."),
    BAD_CONSTANT(BAD_REQUEST, "잘못된 인자입니다."),
    API_REQUEST_TIMEOUT(REQUEST_TIMEOUT, "요청 시간이 초과되었습니다."),
    DUPLICATED_NICKNAME(CONFLICT, "이미 사용중인 닉네임입니다."),
    DUPLICATED_EMAIL(CONFLICT, "이미 사용중인 이메일입니다."),
    DUPLICATED_CONFIRMED(CONFLICT, "이미 읽음 처리된 알림입니다."),
    FILE_NOT_EXISTS(BAD_REQUEST, "파일이 첨부되지 않았습니다."),
    FILE_SIZE_EXCEED(BAD_REQUEST, "업로드 가능한 파일 용량을 초과했습니다."),
    ALREADY_DELETED_MEMBER(NOT_FOUND, "이미 탈퇴된 계정입니다."),
    DATABASE_ERROR(INTERNAL_SERVER_ERROR, "데이터베이스 에러");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
