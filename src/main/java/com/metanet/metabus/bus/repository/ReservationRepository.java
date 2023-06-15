package com.metanet.metabus.bus.repository;

import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.ReservationStatus;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByMemberAndDeletedDateIsNullOrderByDepartureDateDescCreatedDateDesc(Member member);

    List<Reservation> findByMemberAndDeletedDateIsNullAndReservationStatusOrderByDepartureDateDescCreatedDateDesc(Member member, ReservationStatus reservationStatus);

    Reservation findByIdAndDeletedDateIsNull(Long reservationId);

    List<Reservation> findByCreatedDateBeforeAndReservationStatusAndDeletedDateIsNull(LocalDateTime threshold, ReservationStatus reservationStatus);

    Reservation findBySeatId(Seat seat);

    List<Reservation> findByDepartureDateAndDeletedDateIsNullAndReservationStatus(LocalDate yesterday, ReservationStatus reservationStatus);
}
