package com.metanet.metabus.common.exception.unauthorized;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.INVALID_SEAT_COUNT;

public class InvalidSeatCountException extends AbstractAppException {
    public InvalidSeatCountException() {
        super(INVALID_SEAT_COUNT);
    }
}
