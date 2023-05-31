package com.metanet.metabus.bus.dto;

import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReservationInfoRequest {
    private Long busNum;
    private LocalDate departureDate;

    private Long memberId;
    private String departure;
    private String destination;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Long payment;
    private Long[] seatNum;
    private int[] passengerType;

    public Reservation toEntity(Member member, Seat seat, Long payment, String passengerType) {
        return Reservation.builder()
                .member(member)
                .departure(departure)
                .destination(destination)
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .departureDate(departureDate)
                .payment(payment)
                .seatId(seat)
                .passengerType(passengerType)
                .build();
    }
}
