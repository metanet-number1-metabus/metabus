package com.metanet.metabus.common.exception.unauthorized;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.INVALID_SEAT;

public class InvalidSeatException extends AbstractAppException {
    public InvalidSeatException() {
        super(INVALID_SEAT);
    }
}
