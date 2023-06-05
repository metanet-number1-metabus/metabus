package com.metanet.metabus.common.exception.not_found;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.SEAT_NOT_FOUND;

public class SeatNotFoundException extends AbstractAppException {
    public SeatNotFoundException() {
        super(SEAT_NOT_FOUND);
    }
}