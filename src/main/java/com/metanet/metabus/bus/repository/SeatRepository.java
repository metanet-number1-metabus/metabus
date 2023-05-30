package com.metanet.metabus.bus.repository;

import com.metanet.metabus.bus.entity.Bus;
import com.metanet.metabus.bus.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByBus(Bus bus);
}
