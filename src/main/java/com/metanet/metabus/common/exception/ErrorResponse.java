package com.metanet.metabus.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse<T> {
    private ErrorCode errorCode;
    private T result;

    public static <T> ErrorResponse<T> error(ErrorCode errorCode, T result) {
        return new ErrorResponse<>(errorCode, result);
    }
}