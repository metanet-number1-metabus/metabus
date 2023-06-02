package com.metanet.metabus.common.exception.bad_request;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.BAD_DATE;

public class BadDateException extends AbstractAppException {
    public BadDateException() {
        super(BAD_DATE);
    }
}