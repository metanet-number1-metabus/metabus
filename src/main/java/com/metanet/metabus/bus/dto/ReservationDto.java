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
public class ReservationDto {
    private String departure;
    private String destination;
    private String busType;
    private Long busNum;
    private LocalDate departureDate;
    private String departureTime;
    private String arrivalTime;
    private String passengerType;
    private Long seatNum;
    private LocalDate createdDate;
    private Long payment;
}
