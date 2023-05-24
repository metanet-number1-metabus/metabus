package com.metanet.metabus.common.exception.unauthorized;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.INVALID_PASSWORD;

public class InvalidPasswordException extends AbstractAppException {
    public InvalidPasswordException() {
        super(INVALID_PASSWORD);
    }
}