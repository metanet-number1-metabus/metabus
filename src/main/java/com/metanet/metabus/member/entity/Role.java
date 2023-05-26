package com.metanet.metabus.member.entity;

import com.metanet.metabus.common.exception.bad_request.BadConstantException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_USER, ROLE_ADMIN;

    public static Role of(String name) {
        for (Role role : values()) {
            if (role.name().contains(name)) return role;
        }
        throw new BadConstantException();
    }
}
