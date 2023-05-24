package com.metanet.metabus.common.exception.unauthorized;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.INVALID_FILE_FORMAT;

public class InvalidFileFormatException extends AbstractAppException {
    public InvalidFileFormatException() {
        super(INVALID_FILE_FORMAT);
    }
}
