package com.metanet.metabus.bus.service;

import com.metanet.metabus.bus.dto.ReservationInfoRequest;
import com.metanet.metabus.bus.entity.Bus;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.bus.repository.BusRepository;
import com.metanet.metabus.bus.repository.ReservationRepository;
import com.metanet.metabus.bus.repository.SeatRepository;
import com.metanet.metabus.common.exception.not_found.MemberNotFoundException;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final BusRepository busRepository;
    private final MemberRepository memberRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public List<Long> create(ReservationInfoRequest reservationInfoRequest) {

        List<Long> reservationsIds = new ArrayList<>();

        // 1. 버스 - 있으면 pass, 없으면 저장
        Long busNum = reservationInfoRequest.getBusNum();
        LocalDate departureDate = reservationInfoRequest.getDepartureDate();

        Bus bus = createBusByBusNumAndDepartureDate(busNum, departureDate);

        // 2. 예약자 정보
        Long memberId = reservationInfoRequest.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        Long[] seatNum = reservationInfoRequest.getSeatNum();
        String[] passengerType = makePassengerType(reservationInfoRequest);
        Long payment = reservationInfoRequest.getPayment();

        for (int i = 0; i < seatNum.length; i++) {

            // 3. 자리 정보 저장
            Seat seat = seatRepository.save(Seat.of(seatNum[i], bus));

            // 4. 승객 타입에 따른 가격 할인
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

            // 5. 예약 정보 저장
            Reservation reservation = reservationInfoRequest.toEntity(member, seat, payment, passengerType[i]);
            Reservation savedReservation = reservationRepository.save(reservation);
            reservationsIds.add(savedReservation.getId());
        }

        return reservationsIds;
    }

    private Bus createBusByBusNumAndDepartureDate(Long busNum, LocalDate departureDate) {
        // 기존에 존재하는 버스는 검색하여 반환
        // 새로운 버스는 저장하고 반환
        return busRepository.findByBusNumAndDepartureDate(busNum, departureDate).orElseGet(() -> busRepository.save(Bus.of(busNum, departureDate)));
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
