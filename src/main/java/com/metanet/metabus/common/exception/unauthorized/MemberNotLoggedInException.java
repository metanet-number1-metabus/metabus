package com.metanet.metabus.common.exception.unauthorized;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.MEMBER_NOT_LOGGED_IN;

public class MemberNotLoggedInException extends AbstractAppException {
    public MemberNotLoggedInException() {
        super(MEMBER_NOT_LOGGED_IN);
    }
}