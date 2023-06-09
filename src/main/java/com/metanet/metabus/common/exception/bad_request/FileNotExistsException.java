package com.metanet.metabus.common.exception.bad_request;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.FILE_NOT_EXISTS;

public class FileNotExistsException extends AbstractAppException {
    public FileNotExistsException() {
        super(FILE_NOT_EXISTS);
    }
}