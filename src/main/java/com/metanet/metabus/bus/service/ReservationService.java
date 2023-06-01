package com.metanet.metabus.bus.service;

import com.metanet.metabus.bus.dto.ReservationInfoRequest;
import com.metanet.metabus.bus.entity.Bus;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.bus.repository.BusRepository;
import com.metanet.metabus.bus.repository.ReservationRepository;
import com.metanet.metabus.bus.repository.SeatRepository;
import com.metanet.metabus.common.exception.conflict.DuplicateSeatException;
import com.metanet.metabus.common.exception.not_found.MemberNotFoundException;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final BusRepository busRepository;
    private final MemberRepository memberRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public void create(MemberDto memberDto, ReservationInfoRequest reservationInfoRequest) {

        Long memberId = memberDto.getId();
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        String departureTime = makeLocalTime(reservationInfoRequest.getDepartureTime());
        String arrivalTime = makeLocalTime(reservationInfoRequest.getArrivalTime());

        Long busNum = reservationInfoRequest.getBusNum();
        LocalDate departureDate = makeLocalDate(reservationInfoRequest.getDepartureDate());
        Bus bus = createBusByBusNumAndDepartureDate(busNum, departureDate);

        Long payment = reservationInfoRequest.getPayment();
        Long[] seatNum = reservationInfoRequest.getSeatNum();
        String[] passengerType = makePassengerType(reservationInfoRequest);

        for (int i = 0; i < seatNum.length; i++) {

            Seat seat = saveSeatBySeatNumAndBus(seatNum[i], bus);

            switch (passengerType[i]) {
                case "성인":
                    break;
                case "중고생":
                    payment = payment * 8 / 10; // 20% 할인
                    break;
                case "아동":
                    payment = payment * 5 / 10; // 50% 할인
                    break;
            }

            Reservation reservation = reservationInfoRequest.toEntity(member, departureTime, arrivalTime, departureDate, payment, seat, passengerType[i]);
            reservationRepository.save(reservation);
        }

    }

    private String makeLocalTime(String timeString) {

        int hour = Integer.parseInt(timeString.substring(0, 2));
        int minute = Integer.parseInt(timeString.substring(3, 5));
        LocalTime time = LocalTime.of(hour, minute);

        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private LocalDate makeLocalDate(String dateString) {

        int currentYear = LocalDate.now().getYear();
        String newDateString = currentYear + "/" + dateString;

        int year = Integer.parseInt(newDateString.substring(0, 4));
        int month = Integer.parseInt(newDateString.substring(5, 7));
        int day = Integer.parseInt(newDateString.substring(8, 10));

        return LocalDate.of(year, month, day);
    }

    private Bus createBusByBusNumAndDepartureDate(Long busNum, LocalDate departureDate) {
        return busRepository.findByBusNumAndDepartureDate(busNum, departureDate).orElseGet(() -> busRepository.save(Bus.of(busNum, departureDate)));
    }

    private Seat saveSeatBySeatNumAndBus(Long seatNum, Bus bus) {
        Seat seat;
        Optional<Seat> seatBySeatNumAndBus = seatRepository.findBySeatNumAndBus(seatNum, bus);
        if (seatBySeatNumAndBus.isPresent()) {
            throw new DuplicateSeatException();
        } else {
            seat = seatRepository.save(Seat.of(seatNum, bus));
        }
        return seat;
    }

    private String[] makePassengerType(ReservationInfoRequest reservationInfoRequest) {

        int[] pType = reservationInfoRequest.getPassengerType();
        int adult = pType[0];
        int teenager = pType[1];
        int kid = pType[2];

        String[] passengerType = new String[adult + teenager + kid];

        for (int i = 0; i < passengerType.length; i++) {
            if (i < adult) {
                passengerType[i] = "성인";
            } else if (i < adult + teenager) {
                passengerType[i] = "중고생";
            } else {
                passengerType[i] = "아동";
            }
        }

        return passengerType;
    }

}
