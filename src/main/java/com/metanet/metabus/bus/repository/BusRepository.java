package com.metanet.metabus.bus.repository;

import com.metanet.metabus.bus.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface BusRepository extends JpaRepository<Bus, Long> {
    Optional<Bus> findByBusNumAndDepartureDate(Long busNum, LocalDate departureDate);
}
