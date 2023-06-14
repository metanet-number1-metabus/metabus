package com.metanet.metabus.mileage.scheduler;

import com.metanet.metabus.bus.entity.PaymentStatus;
import com.metanet.metabus.bus.entity.Reservation;
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
public class DailyMileageJob implements Job {

    private final ReservationRepository reservationRepository;
    private final MileageRepository mileageRepository;
    private final MileageService mileageService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<Reservation> reservations = reservationRepository.findByDepartureDateAndDeletedDateIsNullAndPaymentStatus(yesterday, PaymentStatus.PAID);

        for (Reservation reservation : reservations) {

            Mileage mileage = mileageService.saveMileage(reservation);
            mileageRepository.save(mileage);
        }
    }

}
