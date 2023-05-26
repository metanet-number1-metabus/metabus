package com.metanet.metabus.bus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BusRouteResponseDto {
    private String departureHome;
    private String destinationHome;
    private String departureDate;
    private String roundTrip;
}
