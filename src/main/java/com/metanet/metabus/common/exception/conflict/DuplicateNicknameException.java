package com.metanet.metabus.common.exception.conflict;

import com.metanet.metabus.common.exception.AbstractAppException;

import static com.metanet.metabus.common.exception.ErrorCode.DUPLICATED_NICKNAME;

public class DuplicateNicknameException extends AbstractAppException {
    public DuplicateNicknameException() {
        super(DUPLICATED_NICKNAME);
    }
}