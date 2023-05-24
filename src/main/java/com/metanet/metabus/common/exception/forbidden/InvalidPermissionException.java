package com.metanet.metabus.common.exception.forbidden;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.INVALID_PERMISSION;

public class InvalidPermissionException extends AbstractAppException {
    public InvalidPermissionException() {
        super(INVALID_PERMISSION);
    }
}