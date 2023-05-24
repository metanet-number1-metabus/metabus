package com.metanet.metabus.common.exception.request_timeout;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.API_REQUEST_TIMEOUT;

public class APIRequestTimeoutException extends AbstractAppException {
    public APIRequestTimeoutException() {
        super(API_REQUEST_TIMEOUT);
    }
}