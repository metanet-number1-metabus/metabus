package com.metanet.metabus.bus.repository;

import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByMemberAndDeletedDateIsNull(Member member);

    @Query("SELECT r FROM Reservation r WHERE r.member = :member AND r.deletedDate IS NULL AND r.paymentStatus = 'UNPAID'")
    List<Reservation> findUnpaidReservationsByMember(Member member);

    @Query("SELECT r FROM Reservation r WHERE r.member = :member AND r.deletedDate IS NULL AND r.paymentStatus = 'PAID'")
    List<Reservation> findPaidReservationsByMember(Member member);

    @Query("SELECT r FROM Reservation r WHERE r.member = :member AND r.deletedDate IS NULL AND r.departureDate < :today")
    List<Reservation> findPastReservationsByMember(Member member, LocalDate today);

    Reservation findByIdAndDeletedDateIsNull(Long reservationId);
}
