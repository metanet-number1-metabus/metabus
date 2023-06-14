package com.metanet.metabus.bus.scheduler;

import com.metanet.metabus.bus.entity.PaymentStatus;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.bus.repository.ReservationRepository;
import com.metanet.metabus.bus.repository.SeatRepository;
import com.metanet.metabus.common.exception.not_found.SeatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MinuteJob implements Job {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
        List<Reservation> expiredEntities = reservationRepository.findByCreatedDateBeforeAndPaymentStatusAndDeletedDateIsNull(threshold, PaymentStatus.UNPAID);

        for (Reservation reservation : expiredEntities) {
            Seat seat = seatRepository.findById(reservation.getSeatId().getId()).orElseThrow(SeatNotFoundException::new);
            seat.delete();
            seatRepository.save(seat);
            reservation.delete();
            reservationRepository.save(reservation);
        }
    }
}

