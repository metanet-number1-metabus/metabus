package com.metanet.metabus.bus.repository;

import com.metanet.metabus.bus.entity.PaymentStatus;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByMemberAndDeletedDateIsNullOrderByDepartureDateDesc(Member member);

    @Query("SELECT r FROM Reservation r WHERE r.member = :member AND r.deletedDate IS NULL AND r.paymentStatus = 'UNPAID' AND r.departureDate >= :today")
    List<Reservation> findUnpaidReservationsByMemberOrderByDepartureDateDesc(Member member, LocalDate today);

    @Query("SELECT r FROM Reservation r WHERE r.member = :member AND r.deletedDate IS NULL AND r.paymentStatus = 'PAID' AND r.departureDate >= :today")
    List<Reservation> findPaidReservationsByMemberOrderByDepartureDateDesc(Member member, LocalDate today);

    @Query("SELECT r FROM Reservation r WHERE r.member = :member AND r.deletedDate IS NULL AND r.departureDate < :today")
    List<Reservation> findPastReservationsByMemberOrderByDepartureDateDesc(Member member, LocalDate today);

    Reservation findByIdAndDeletedDateIsNull(Long reservationId);

    List<Reservation> findByCreatedDateBeforeAndPaymentStatusAndDeletedDateIsNull(LocalDateTime threshold, PaymentStatus paymentStatus);

    Reservation findBySeatId(Seat seat);

    List<Reservation> findByDepartureDateAndDeletedDateIsNullAndPaymentStatus(LocalDate yesterday, PaymentStatus paymentStatus);
}
