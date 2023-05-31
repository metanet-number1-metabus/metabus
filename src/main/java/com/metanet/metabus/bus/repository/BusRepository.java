package com.metanet.metabus.bus.repository;

import com.metanet.metabus.bus.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BusRepository extends JpaRepository<Bus, Long> {
    Bus findByBusNumAndDepartureDate(Long busNum, LocalDate departureDate);
}
