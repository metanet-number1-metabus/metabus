package com.metanet.metabus.bus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BusPopularRoutesRequest {
    private Long busNum;
    private LocalDate departureDate;
    private Long seatNum;

}
