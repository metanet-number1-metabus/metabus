package com.metanet.metabus.bus.repository;

import com.metanet.metabus.bus.entity.Bus;
import com.metanet.metabus.bus.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByBus(Bus bus);

    Optional<Seat> findBySeatNumAndBus(Long seatNum, Bus bus);
}
