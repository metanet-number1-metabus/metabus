package com.metanet.metabus.member.repository;


import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.ReservationStatus;
import com.metanet.metabus.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface MyPageRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMemberAndDeletedDateIsNullAndReservationStatus(Member member, ReservationStatus reservationStatus);

    @Query("SELECT m.createdDate FROM Member m WHERE m.id = :id")
    Timestamp myDates(Long id);

    @Query("SELECT m.grade FROM Member m WHERE m.id= :id")
    String myGrede(Long id);

    @Query("SELECT m.mileage FROM Member m WHERE m.id= :id")
    Long myMileage(Long id);
}
