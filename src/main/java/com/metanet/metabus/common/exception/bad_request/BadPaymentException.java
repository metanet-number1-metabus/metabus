package com.metanet.metabus.common.exception.bad_request;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.BAD_PAYMENT;

public class BadPaymentException extends AbstractAppException {
    public BadPaymentException() {
        super(BAD_PAYMENT);
    }
}