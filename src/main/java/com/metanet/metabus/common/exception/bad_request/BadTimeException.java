package com.metanet.metabus.common.exception.bad_request;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.BAD_TIME;

public class BadTimeException extends AbstractAppException {
    public BadTimeException() {
        super(BAD_TIME);
    }
}