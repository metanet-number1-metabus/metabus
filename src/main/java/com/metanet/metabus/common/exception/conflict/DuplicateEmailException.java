package com.metanet.metabus.common.exception.conflict;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.DUPLICATED_EMAIL;

public class DuplicateEmailException extends AbstractAppException {
    public DuplicateEmailException() {
        super(DUPLICATED_EMAIL);
    }
}