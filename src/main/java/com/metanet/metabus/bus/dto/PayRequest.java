package com.metanet.metabus.bus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PayRequest {
    private String applyNum;
    private Long memberId;
    private String cardName;
    private String cardNum;
    private String impUid;
    private String merchantName;
    private Long payment;
    private String payMethod;
    private String reservationIds;

}
