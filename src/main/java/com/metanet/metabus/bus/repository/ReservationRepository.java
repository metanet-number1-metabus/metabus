package com.metanet.metabus.bus.repository;

import com.metanet.metabus.bus.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
