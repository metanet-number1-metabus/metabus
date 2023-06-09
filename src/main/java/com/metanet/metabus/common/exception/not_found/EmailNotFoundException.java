package com.metanet.metabus.common.exception.not_found;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.EMAIL_NOT_FOUND;

public class EmailNotFoundException extends AbstractAppException {
    public EmailNotFoundException() {
        super(EMAIL_NOT_FOUND);
    }
}
