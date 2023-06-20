package com.metanet.metabus.bus.service;

import com.metanet.metabus.bus.entity.Bus;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.ReservationStatus;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.bus.repository.BusRepository;
import com.metanet.metabus.bus.repository.ReservationRepository;
import com.metanet.metabus.bus.repository.SeatRepository;
import com.metanet.metabus.common.exception.not_found.BusNotFoundException;
import com.metanet.metabus.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class SeatServiceTest {

    private final SeatRepository seatRepository = Mockito.mock(SeatRepository.class);
    private final BusRepository busRepository = Mockito.mock(BusRepository.class);
    private final ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);

    private final SeatService seatService = new SeatService(seatRepository, busRepository, reservationRepository);

    private final Long busNum = 10000L;
    private final LocalDate departureDate = LocalDate.now().plusDays(30);
    private final String strDepartureDate = departureDate.format(DateTimeFormatter.ofPattern("MM/dd"));
    private final Long[] seatNum = new Long[]{1L, 2L};

    private final Bus bus = Bus.builder()
            .id(1L)
            .busNum(busNum)
            .departureDate(departureDate)
            .build();

    private final Seat seat = Seat.builder()
            .id(1L)
            .seatNum(1L)
            .bus(bus)
            .build();

    private final Member member = Member.builder()
            .id(1L)
            .build();

    private final Reservation reservation = Reservation.builder()
            .id(1L)
            .member(member)
            .departure("간성")
            .destination("동서울")
            .departureTime("12:30")
            .arrivalTime("16:01")
            .departureDate(departureDate)
            .payment(21100L)
            .seatId(seat)
            .passengerType("성인")
            .reservationStatus(ReservationStatus.UNPAID)
            .busType("일반")
            .busId(bus.getId())
            .usedMileage(0L)
            .build();

    @Test
    @DisplayName("좌석 얻기 성공: 예약된 자리 return")
    void read_seat_success1() {

        List<Seat> seats = new ArrayList<>();
        seats.add(seat);

        when(busRepository.findByBusNumAndDepartureDate(busNum, departureDate)).thenReturn(Optional.of(bus));
        when(seatRepository.findByBus(bus)).thenReturn(seats);
        when(reservationRepository.findBySeatId(seat)).thenReturn(reservation);

        List<Long> result = seatService.read(busNum, departureDate);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0));
    }

    @Test
    @DisplayName("좌석 얻기 성공: 빈 리스트 return")
    void read_seat_success2() {

        List<Seat> seats = new ArrayList<>();

        when(busRepository.findByBusNumAndDepartureDate(busNum, departureDate)).thenThrow(BusNotFoundException.class);

        List<Long> result = seatService.read(busNum, departureDate);

        assertEquals(0, result.size());
    }

}