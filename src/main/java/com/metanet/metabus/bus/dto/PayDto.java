package com.metanet.metabus.bus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PayDto {
    private String merchantUid;
    private String reservationIds;
}
