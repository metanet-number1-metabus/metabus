package com.metanet.metabus.common.exception;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AbstractAppException.class)
    public ResponseEntity<?> abstractBaseExceptionHandler(AbstractAppException e) {
        logger.error("{} {}", e.getErrorCode().name(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(ErrorResponse.error(e.getErrorCode(),e));
    }
}