package com.metanet.metabus.scheduler;

import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.ReservationStatus;
import com.metanet.metabus.bus.repository.ReservationRepository;
import com.metanet.metabus.mileage.entity.Mileage;
import com.metanet.metabus.mileage.repository.MileageRepository;
import com.metanet.metabus.mileage.service.MileageService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DailyJob implements Job {

    private final ReservationRepository reservationRepository;
    private final MileageRepository mileageRepository;
    private final MileageService mileageService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<Reservation> reservations = reservationRepository.findByDepartureDateAndDeletedDateIsNullAndReservationStatus(yesterday, ReservationStatus.PAID);

        for (Reservation reservation : reservations) {
            reservation.updateExpiredStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);

            Mileage mileage = mileageService.saveMileage(reservation);
            mileageRepository.save(mileage);
        }
    }

}
