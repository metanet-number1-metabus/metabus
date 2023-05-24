package com.metanet.metabus.common.exception.conflict;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.DUPLICATED_CONFIRMED;

public class DuplicateConfirmedException extends AbstractAppException {
    public DuplicateConfirmedException() {
        super(DUPLICATED_CONFIRMED);
    }
}