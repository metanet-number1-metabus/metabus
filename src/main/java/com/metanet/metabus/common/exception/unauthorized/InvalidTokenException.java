package com.metanet.metabus.common.exception.unauthorized;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.INVALID_TOKEN;

public class InvalidTokenException extends AbstractAppException {
    public InvalidTokenException() {
        super(INVALID_TOKEN);
    }
}