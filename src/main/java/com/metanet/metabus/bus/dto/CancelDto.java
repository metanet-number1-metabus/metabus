package com.metanet.metabus.bus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CancelDto {

    private String impUid;
    private String reservationIds;
    private String paymentSum;

}
