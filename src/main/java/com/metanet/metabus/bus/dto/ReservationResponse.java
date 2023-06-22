package com.metanet.metabus.bus.dto;

import com.metanet.metabus.bus.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ReservationResponse {
    private ReservationStatus reservationStatus;
    private LocalDate departureDate;
    private String departureTime;
    private String departure;
    private String destination;
    private Long payment;
    private Long usedMileage;
    private Long id;
    private String impUid;
    private Long busId;

}
