package com.metanet.metabus.bus.service;

import com.metanet.metabus.bus.entity.Bus;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.bus.repository.BusRepository;
import com.metanet.metabus.bus.repository.ReservationRepository;
import com.metanet.metabus.bus.repository.SeatRepository;
import com.metanet.metabus.common.exception.not_found.BusNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final BusRepository busRepository;
    private final ReservationRepository reservationRepository;

    public List<Long> read(Long busNum, LocalDate departureDate) {
        try {
            Bus bus = busRepository.findByBusNumAndDepartureDate(busNum, departureDate).orElseThrow(BusNotFoundException::new);

            List<Seat> seats = seatRepository.findByBus(bus);
            List<Long> list = new ArrayList<>();

            for (Seat seat : seats) {
                Reservation reservation = reservationRepository.findBySeatId(seat);

                if (reservation.getDeletedDate() == null) {
                    list.add(seat.getSeatNum());
                }
            }

            return list;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

}
