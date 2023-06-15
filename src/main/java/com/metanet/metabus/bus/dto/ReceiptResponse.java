package com.metanet.metabus.bus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ReceiptResponse {
    private String applyNum;
    private String memberEmail;
    private String memberName;
    private String memberPhoneNum;
    private String cardName;
    private String cardNum;
    private String impUid;
    private String merchantName;
    private Long payment;
    private String payMethod;

}
