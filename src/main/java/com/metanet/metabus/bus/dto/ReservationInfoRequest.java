package com.metanet.metabus.bus.dto;

import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReservationInfoRequest {
    private Long busNum;
    private String departureDate;

    private String departure;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private Long payment;
    private Long[] seatNum;
    private int[] passengerType;

    public Reservation toEntity(Member member, String departureTime, String arrivalTime, LocalDate departureDate, Long payment, Seat seat, String passengerType) {
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
