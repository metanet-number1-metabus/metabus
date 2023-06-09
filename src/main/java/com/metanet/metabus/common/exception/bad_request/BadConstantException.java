package com.metanet.metabus.common.exception.bad_request;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.BAD_CONSTANT;

public class BadConstantException extends AbstractAppException {
    public BadConstantException() {
        super(BAD_CONSTANT);
    }
}