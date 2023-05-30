package com.metanet.metabus.bus.service;

import com.metanet.metabus.bus.entity.Bus;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.bus.repository.BusRepository;
import com.metanet.metabus.bus.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;
    private final BusRepository busRepository;

    public List<Seat> read(Long busNum, LocalDate departureDate) {
        Bus bus = busRepository.findByBusNumAndDepartureDate(busNum, departureDate);
        return seatRepository.findByBus(bus);
    }

}
