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
public class ReservationStatusRequest {
    private Long memberId;
    private LocalDate createdDate;
    private LocalDate departureDate;
    private Long payment;
    private ReservationStatus reservationStatus;
    private LocalDate deletedDate;
}
