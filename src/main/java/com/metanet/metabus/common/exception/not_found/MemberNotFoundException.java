package com.metanet.metabus.common.exception.not_found;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.MEMBER_NOT_FOUND;

public class MemberNotFoundException extends AbstractAppException {
    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND);
    }
}