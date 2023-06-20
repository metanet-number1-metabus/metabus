package com.metanet.metabus.bus.service;

import com.metanet.metabus.bus.dto.ReservationDto;
import com.metanet.metabus.bus.dto.ReservationInfoRequest;
import com.metanet.metabus.bus.entity.Bus;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.ReservationStatus;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.bus.repository.BusRepository;
import com.metanet.metabus.bus.repository.ReservationRepository;
import com.metanet.metabus.bus.repository.SeatRepository;
import com.metanet.metabus.common.exception.bad_request.BadDateException;
import com.metanet.metabus.common.exception.bad_request.BadPaymentException;
import com.metanet.metabus.common.exception.bad_request.BadTimeException;
import com.metanet.metabus.common.exception.conflict.DuplicateSeatException;
import com.metanet.metabus.common.exception.not_found.MemberNotFoundException;
import com.metanet.metabus.common.exception.not_found.SeatNotFoundException;
import com.metanet.metabus.common.exception.unauthorized.InvalidSeatCountException;
import com.metanet.metabus.common.exception.unauthorized.InvalidSeatException;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final BusRepository busRepository;
    private final MemberRepository memberRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public List<Long> create(MemberDto memberDto, ReservationInfoRequest reservationInfoRequest) {

        List<Long> reservationIdList = new ArrayList<>();

        Member member = getMember(memberDto);

        String departureTime = makeLocalTime(reservationInfoRequest.getDepartureTime());
        String arrivalTime = makeLocalTime(reservationInfoRequest.getArrivalTime());

        Long busNum = reservationInfoRequest.getBusNum();
        LocalDate departureDate = verifyLocalDate(makeLocalDate(reservationInfoRequest.getDepartureDate()));
        Bus bus = createBusByBusNumAndDepartureDate(busNum, departureDate);

        Long[] seatNum = reservationInfoRequest.getSeatNum();

        int seatCount = verifySeatCount(seatNum.length);

        int maxSeatNum = getMaxSeatNum(reservationInfoRequest.getBusType());

        String[] passengerType = makePassengerType(reservationInfoRequest);

        ReservationStatus reservationStatus = ReservationStatus.UNPAID;

        for (int i = 0; i < seatCount; i++) {

            if (seatNum[i] < 1 || seatNum[i] > maxSeatNum) {
                throw new InvalidSeatException();
            } else {
                Long payment = reservationInfoRequest.getPayment();
                Seat seat = saveSeatBySeatNumAndBus(seatNum[i], bus);

                if (payment == null || payment == 0) {
                    throw new BadPaymentException();

                } else {

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
                }

                Reservation reservation = reservationInfoRequest.toEntity(member, departureTime, arrivalTime, departureDate, payment, seat, passengerType[i], reservationStatus, bus.getId());
                Reservation savedReservation = reservationRepository.save(reservation);
                reservationIdList.add(savedReservation.getId());
            }

        }

        return reservationIdList;

    }

    public List<Reservation> readAllReservation(MemberDto memberDto) {
        Member member = getMember(memberDto);

        return reservationRepository.findByMemberAndDeletedDateIsNullOrderByCreatedDateDesc(member);
    }

    public List<Reservation> readUnpaidReservation(MemberDto memberDto) {
        Member member = getMember(memberDto);

        return reservationRepository.findByMemberAndDeletedDateIsNullAndReservationStatusOrderByDepartureDateDescCreatedDateDesc(member, ReservationStatus.UNPAID);
    }

    public List<Reservation> readPaidReservation(MemberDto memberDto) {
        Member member = getMember(memberDto);

        return reservationRepository.findByMemberAndDeletedDateIsNullAndReservationStatusOrderByDepartureDateDescCreatedDateDesc(member, ReservationStatus.PAID);
    }

    public List<Reservation> readPastReservation(MemberDto memberDto) {
        Member member = getMember(memberDto);

        return reservationRepository.findByMemberAndDeletedDateIsNullAndReservationStatusOrderByDepartureDateDescCreatedDateDesc(member, ReservationStatus.EXPIRED);

    }

    public List<ReservationDto> readByReservationId(Long[] reservationIds) {

        List<ReservationDto> reservationList = new ArrayList<>();

        for (Long reservationId : reservationIds) {
            ReservationDto reservationDTO = readReservationDetail(reservationId);
            reservationList.add(reservationDTO);
        }

        return reservationList;

    }

    public ReservationDto readReservationDetail(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndDeletedDateIsNull(reservationId);
        Long seatId = reservation.getSeatId().getId();
        Seat seat = seatRepository.findById(seatId).orElseThrow(SeatNotFoundException::new);
        Long busNum = seat.getBus().getBusNum();
        Long seatNum = seat.getSeatNum();

        LocalDate createdDate = reservation.getCreatedDate().toLocalDate();

        return ReservationDto.builder()
                .departure(reservation.getDeparture())
                .destination(reservation.getDestination())
                .busType(reservation.getBusType())
                .busNum(busNum)
                .departureDate(reservation.getDepartureDate())
                .departureTime(reservation.getDepartureTime())
                .arrivalTime(reservation.getArrivalTime())
                .passengerType(reservation.getPassengerType())
                .seatNum(seatNum)
                .createdDate(createdDate)
                .payment(reservation.getPayment())
                .impUid(reservation.getImpUid())
                .build();
    }

    public Long getPaymentSum(Long[] reservationIds) {

        Long paymentSum = 0L;

        for (Long reservationId : reservationIds) {
            Reservation reservation = reservationRepository.findByIdAndDeletedDateIsNull(reservationId);
            paymentSum += reservation.getPayment();
        }

        return paymentSum;
    }

    public Long getUsedMileageSum(Long[] reservationIds) {

        Long usedMileageSum = 0L;

        for (Long reservationId : reservationIds) {
            Reservation reservation = reservationRepository.findByIdAndDeletedDateIsNull(reservationId);
            usedMileageSum += reservation.getUsedMileage();
        }

        return usedMileageSum;
    }

    public String getStrReservationIds(Long[] reservationIds) {
        StringBuilder strReservationIds = new StringBuilder();

        for (int i = 0; i < reservationIds.length; i++) {
            strReservationIds.append(reservationIds[i]);

            if (i != reservationIds.length - 1) {
                strReservationIds.append(", ");
            }
        }

        return strReservationIds.toString();
    }

    public Member getMember(Long[] reservationIds) {

        Reservation reservation = reservationRepository.findByIdAndDeletedDateIsNull(reservationIds[0]);
        return reservation.getMember();

    }

    public Long getMileage(MemberDto memberDto) {
        Member member = getMember(memberDto);

        return member.getMileage();
    }

    private String makeLocalTime(String timeString) {

        if (timeString.matches("\\d{2}시\\d{2}분")) {
            int hour = Integer.parseInt(timeString.substring(0, 2));
            int minute = Integer.parseInt(timeString.substring(3, 5));

            if (!(hour >= 0 && hour <= 23) || !(minute >= 0 && minute <= 59)) {
                throw new BadTimeException();

            } else {
                LocalTime time = LocalTime.of(hour, minute);

                return time.format(DateTimeFormatter.ofPattern("HH:mm"));
            }

        } else {
            throw new BadTimeException();
        }
    }

    private LocalDate makeLocalDate(String dateString) {

        if (dateString.matches("\\d{2}/\\d{2}")) {
            int currentYear = LocalDate.now().getYear();

            int month = Integer.parseInt(dateString.substring(0, 2));
            int day = Integer.parseInt(dateString.substring(3, 5));

            return LocalDate.of(currentYear, month, day);

        } else {
            throw new BadDateException();
        }
    }

    private LocalDate verifyLocalDate(LocalDate date) {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(60);

        if (date.isAfter(startDate) && date.isBefore(endDate)) {
            return date;

        } else {
            throw new BadDateException();
        }

    }

    private Bus createBusByBusNumAndDepartureDate(Long busNum, LocalDate departureDate) {
        return busRepository.findByBusNumAndDepartureDate(busNum, departureDate).orElseGet(() -> busRepository.save(Bus.of(busNum, departureDate)));
    }

    private int verifySeatCount(int seatCount) {
        if (seatCount > 5) {
            throw new InvalidSeatCountException();
        }

        return seatCount;
    }

    private int getMaxSeatNum(String busType) {
        int maxSeatNum = 0;

        if (busType.contains("일반")) {
            maxSeatNum = 41;
        } else if (busType.contains("우등") || busType.contains("프리")) {
            maxSeatNum = 31;
        }

        return maxSeatNum;
    }

    private Seat saveSeatBySeatNumAndBus(Long seatNum, Bus bus) {
        Seat seat;
        Optional<Seat> seatBySeatNumAndBus = seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(seatNum, bus);

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

    private Member getMember(MemberDto memberDto) {
        Long memberId = memberDto.getId();
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }

}
