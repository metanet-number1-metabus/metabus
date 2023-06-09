package com.metanet.metabus.common.exception.not_found;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.ALREADY_DELETED_MEMBER;

public class AlreadyDeletedMemberException extends AbstractAppException {
    public AlreadyDeletedMemberException() {
        super(ALREADY_DELETED_MEMBER);
    }
}