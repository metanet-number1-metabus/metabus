package com.metanet.metabus.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(AbstractAppException.class)
    public ResponseEntity<?> abstractBaseExceptionHandler(AbstractAppException e) {
        log.error("{} {}", e.getErrorCode().name(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(ErrorResponse.error(e.getMessage()));
    }

//    @ExceptionHandler(AbstractAppException.class)
//    public String abstractBaseExceptionHandler(AbstractAppException e) {
//        log.error("{} {}", e.getErrorCode().name(), e.getMessage());
//        return e.getMessage();
//
//    }
}