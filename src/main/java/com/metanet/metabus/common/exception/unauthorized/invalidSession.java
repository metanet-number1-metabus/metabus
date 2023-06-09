package com.metanet.metabus.common.exception.unauthorized;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.CHAT_ERROR;

public class invalidSession extends AbstractAppException {

    public invalidSession() {
        super(CHAT_ERROR);
    }
}
