package com.metanet.metabus.common.exception.conflict;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.DUPLICATED_SEAT;

public class DuplicateSeatException extends AbstractAppException {
    public DuplicateSeatException() {
        super(DUPLICATED_SEAT);
    }
}