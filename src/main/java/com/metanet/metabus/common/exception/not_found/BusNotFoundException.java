package com.metanet.metabus.common.exception.not_found;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.BUS_NOT_FOUND;

public class BusNotFoundException extends AbstractAppException {
    public BusNotFoundException() {
        super(BUS_NOT_FOUND);
    }
}