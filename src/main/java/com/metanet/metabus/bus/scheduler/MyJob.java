package com.metanet.metabus.bus.scheduler;

import com.metanet.metabus.bus.entity.PaymentStatus;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.repository.ReservationRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MyJob implements Job {

    private final ReservationRepository reservationRepository;

    @Autowired
    public MyJob(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
        List<Reservation> expiredEntities = reservationRepository.findByCreatedDateBeforeAndPaymentStatusAndDeletedDateIsNull(threshold, PaymentStatus.UNPAID);

        for (Reservation reservation : expiredEntities) {
            reservation.delete();
            reservationRepository.save(reservation);
        }
    }
}

