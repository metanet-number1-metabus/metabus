package com.metanet.metabus.common.exception.internal_server_error;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.DATABASE_ERROR;

public class DatabaseException extends AbstractAppException {
    public DatabaseException() {
        super(DATABASE_ERROR);
    }
}