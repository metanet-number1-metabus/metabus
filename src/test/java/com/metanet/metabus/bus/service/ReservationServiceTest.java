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
import com.metanet.metabus.common.exception.not_found.SeatNotFoundException;
import com.metanet.metabus.common.exception.unauthorized.InvalidSeatCountException;
import com.metanet.metabus.common.exception.unauthorized.InvalidSeatException;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ReservationServiceTest {

    private final BusRepository busRepository = Mockito.mock(BusRepository.class);
    private final MemberRepository memberRepository = Mockito.mock(MemberRepository.class);
    private final SeatRepository seatRepository = Mockito.mock(SeatRepository.class);
    private final ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);
    private final ReservationService reservationService = new ReservationService(busRepository, memberRepository, seatRepository, reservationRepository);

    private final Long busNum = 10000L;
    private final LocalDate departureDate = LocalDate.now().plusDays(30);
    private final String strDepartureDate = departureDate.format(DateTimeFormatter.ofPattern("MM/dd"));
    private final Long[] seatNum = new Long[]{1L, 2L};
    private final LocalDateTime createdDate = LocalDateTime.now();
    private final Long[] reservationIds = new Long[]{1L};

    private final MemberDto memberDto = MemberDto.builder()
            .id(1L)
            .build();

    private final Member member = Member.builder()
            .id(1L)
            .build();

    private final ReservationInfoRequest adultAndStandardReservationReq = ReservationInfoRequest.builder()
            .departureTime("12시30분")
            .arrivalTime("16시01분")
            .busNum(busNum)
            .departureDate(strDepartureDate)
            .departure("간성")
            .destination("동서울")
            .payment(21100L)
            .seatNum(seatNum)
            .passengerType(new int[]{2, 0, 0})
            .busType("일반")
            .build();

    private final ReservationInfoRequest adultAndPremiumReservationReq = ReservationInfoRequest.builder()
            .departureTime("12시30분")
            .arrivalTime("16시01분")
            .busNum(busNum)
            .departureDate(strDepartureDate)
            .departure("간성")
            .destination("동서울")
            .payment(21100L)
            .seatNum(seatNum)
            .passengerType(new int[]{2, 0, 0})
            .busType("프리")
            .build();

    private final ReservationInfoRequest teenagerReservationReq = ReservationInfoRequest.builder()
            .departureTime("12시30분")
            .arrivalTime("16시01분")
            .busNum(busNum)
            .departureDate(strDepartureDate)
            .departure("간성")
            .destination("동서울")
            .payment(21100L)
            .seatNum(seatNum)
            .passengerType(new int[]{0, 2, 0})
            .busType("일반")
            .build();

    private final ReservationInfoRequest kidReservationReq = ReservationInfoRequest.builder()
            .departureTime("12시30분")
            .arrivalTime("16시01분")
            .busNum(busNum)
            .departureDate(strDepartureDate)
            .departure("간성")
            .destination("동서울")
            .payment(21100L)
            .seatNum(seatNum)
            .passengerType(new int[]{0, 0, 2})
            .busType("일반")
            .build();

    private final Bus bus = Bus.builder()
            .id(1L)
            .busNum(busNum)
            .departureDate(departureDate)
            .build();

    private final Seat seat = Seat.builder()
            .id(1L)
            .seatNum(1L)
            .bus(bus)
            .build();

    private final ReservationDto reservationDto = ReservationDto.builder()
            .departure("간성")
            .destination("동서울")
            .busType("일반")
            .busNum(busNum)
            .departureDate(departureDate)
            .departureTime("12:30")
            .arrivalTime("16:01")
            .passengerType("성인")
            .seatNum(seat.getSeatNum())
            .createdDate(createdDate.toLocalDate())
            .payment(21100L)
            .build();

    private final Reservation reservation = Reservation.builder()
            .id(1L)
            .member(member)
            .departure("간성")
            .destination("동서울")
            .departureTime("12:30")
            .arrivalTime("16:01")
            .departureDate(departureDate)
            .payment(21100L)
            .seatId(seat)
            .passengerType("성인")
            .reservationStatus(ReservationStatus.UNPAID)
            .busType("일반")
            .busId(bus.getId())
            .usedMileage(0L)
            .build();

    /* ========== 버스 예약 ========== */
    @Test
    @DisplayName("버스 예약 성공: 성인, 일반, 버스O")
    void create_success_adult_and_normal_and_bus_exist() {
        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(busRepository.findByBusNumAndDepartureDate(any(Long.class), any(LocalDate.class))).thenReturn(Optional.of(bus));
        when(seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(any(Long.class), any(Bus.class))).thenReturn(Optional.empty());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        List<Long> result = reservationService.create(memberDto, adultAndStandardReservationReq);

        assertNotNull(result);
        assertFalse(result.isEmpty());

    }

    @Test
    @DisplayName("버스 예약 성공: 성인, 일반, 버스X")
    void create_success_adult_and_normal_and_bus_none() {
        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(busRepository.findByBusNumAndDepartureDate(any(Long.class), any(LocalDate.class))).thenReturn(Optional.empty());
        when(seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(any(Long.class), any(Bus.class))).thenReturn(Optional.empty());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        List<Long> result = reservationService.create(memberDto, adultAndStandardReservationReq);

        assertNotNull(result);
        assertFalse(result.isEmpty());

    }

    @Test
    @DisplayName("버스 예약 성공: 성인, 프리")
    void create_success_adult_and_premium() {
        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(busRepository.findByBusNumAndDepartureDate(any(Long.class), any(LocalDate.class))).thenReturn(Optional.of(bus));
        when(seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(any(Long.class), any(Bus.class))).thenReturn(Optional.empty());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        List<Long> result = reservationService.create(memberDto, adultAndPremiumReservationReq);

        assertNotNull(result);
        assertFalse(result.isEmpty());

    }

    @Test
    @DisplayName("버스 예약 성공: 중고생")
    void create_success_teenager() {
        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(busRepository.findByBusNumAndDepartureDate(any(Long.class), any(LocalDate.class))).thenReturn(Optional.of(bus));
        when(seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(any(Long.class), any(Bus.class))).thenReturn(Optional.empty());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        List<Long> result = reservationService.create(memberDto, teenagerReservationReq);

        assertNotNull(result);
        assertFalse(result.isEmpty());

    }

    @Test
    @DisplayName("버스 예약 성공: 아동")
    void create_success_kid() {
        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(busRepository.findByBusNumAndDepartureDate(any(Long.class), any(LocalDate.class))).thenReturn(Optional.of(bus));
        when(seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(any(Long.class), any(Bus.class))).thenReturn(Optional.empty());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        List<Long> result = reservationService.create(memberDto, kidReservationReq);

        assertNotNull(result);
        assertFalse(result.isEmpty());

    }

    @Test
    @DisplayName("버스 예약 실패: 존재하지 않는 좌석")
    void create_fail_invalid_seat() {

        ReservationInfoRequest reservationInfoRequest = ReservationInfoRequest.builder()
                .departureTime("12시30분")
                .arrivalTime("16시01분")
                .busNum(busNum)
                .departureDate(strDepartureDate)
                .departure("간성")
                .destination("동서울")
                .payment(21100L)
                .seatNum(new Long[]{-1L, 50L})
                .passengerType(new int[]{2, 0, 0})
                .busType("일반")
                .build();

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(busRepository.findByBusNumAndDepartureDate(any(Long.class), any(LocalDate.class))).thenReturn(Optional.of(bus));
        when(seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(any(Long.class), any(Bus.class))).thenReturn(Optional.empty());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        assertThrows(InvalidSeatException.class, () -> reservationService.create(memberDto, reservationInfoRequest));

    }

    @Test
    @DisplayName("버스 예약 실패: 잘못된 형식의 시간1")
    void create_fail_bad_time1() {

        ReservationInfoRequest reservationInfoRequest = ReservationInfoRequest.builder()
                .departureTime("25시30분")
                .arrivalTime("16시01분")
                .busNum(busNum)
                .departureDate(strDepartureDate)
                .departure("간성")
                .destination("동서울")
                .payment(21100L)
                .seatNum(seatNum)
                .passengerType(new int[]{2, 0, 0})
                .busType("일반")
                .build();

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(busRepository.findByBusNumAndDepartureDate(any(Long.class), any(LocalDate.class))).thenReturn(Optional.of(bus));
        when(seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(any(Long.class), any(Bus.class))).thenReturn(Optional.empty());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        assertThrows(BadTimeException.class, () -> reservationService.create(memberDto, reservationInfoRequest));

    }

    @Test
    @DisplayName("버스 예약 실패: 잘못된 형식의 시간2")
    void create_fail_bad_time2() {

        ReservationInfoRequest reservationInfoRequest = ReservationInfoRequest.builder()
                .departureTime("12:30")
                .arrivalTime("16시01분")
                .busNum(busNum)
                .departureDate(strDepartureDate)
                .departure("간성")
                .destination("동서울")
                .payment(21100L)
                .seatNum(seatNum)
                .passengerType(new int[]{2, 0, 0})
                .busType("일반")
                .build();

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(busRepository.findByBusNumAndDepartureDate(any(Long.class), any(LocalDate.class))).thenReturn(Optional.of(bus));
        when(seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(any(Long.class), any(Bus.class))).thenReturn(Optional.empty());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        assertThrows(BadTimeException.class, () -> reservationService.create(memberDto, reservationInfoRequest));

    }

    @Test
    @DisplayName("버스 예약 실패: 잘못된 형식의 날짜1")
    void create_fail_bad_date1() {

        LocalDate departureDate = LocalDate.now().plusDays(30);
        String strDepartureDate = departureDate.format(DateTimeFormatter.ofPattern("MM월dd일"));

        ReservationInfoRequest reservationInfoRequest = ReservationInfoRequest.builder()
                .departureTime("12시30분")
                .arrivalTime("16시01분")
                .busNum(busNum)
                .departureDate(strDepartureDate)
                .departure("간성")
                .destination("동서울")
                .payment(21100L)
                .seatNum(seatNum)
                .passengerType(new int[]{2, 0, 0})
                .busType("일반")
                .build();

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(busRepository.findByBusNumAndDepartureDate(any(Long.class), any(LocalDate.class))).thenReturn(Optional.of(bus));
        when(seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(any(Long.class), any(Bus.class))).thenReturn(Optional.empty());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        assertThrows(BadDateException.class, () -> reservationService.create(memberDto, reservationInfoRequest));

    }

    @Test
    @DisplayName("버스 예약 실패: 잘못된 형식의 날짜2")
    void create_fail_bad_date2() {

        LocalDate departureDate = LocalDate.now().plusDays(90);
        String strDepartureDate = departureDate.format(DateTimeFormatter.ofPattern("MM/dd"));

        ReservationInfoRequest reservationInfoRequest = ReservationInfoRequest.builder()
                .departureTime("12시30분")
                .arrivalTime("16시01분")
                .busNum(busNum)
                .departureDate(strDepartureDate)
                .departure("간성")
                .destination("동서울")
                .payment(21100L)
                .seatNum(seatNum)
                .passengerType(new int[]{2, 0, 0})
                .busType("일반")
                .build();

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(busRepository.findByBusNumAndDepartureDate(any(Long.class), any(LocalDate.class))).thenReturn(Optional.of(bus));
        when(seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(any(Long.class), any(Bus.class))).thenReturn(Optional.empty());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        assertThrows(BadDateException.class, () -> reservationService.create(memberDto, reservationInfoRequest));

    }

    @Test
    @DisplayName("버스 예약 실패: 최대 좌석 수 초과")
    void create_fail_invalid_seat_count() {

        ReservationInfoRequest reservationInfoRequest = ReservationInfoRequest.builder()
                .departureTime("12시30분")
                .arrivalTime("16시01분")
                .busNum(busNum)
                .departureDate(strDepartureDate)
                .departure("간성")
                .destination("동서울")
                .payment(21100L)
                .seatNum(new Long[]{1L, 2L, 3L, 4L, 5L, 6L})
                .passengerType(new int[]{2, 0, 0})
                .busType("일반")
                .build();

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(busRepository.findByBusNumAndDepartureDate(any(Long.class), any(LocalDate.class))).thenReturn(Optional.of(bus));
        when(seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(any(Long.class), any(Bus.class))).thenReturn(Optional.empty());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        assertThrows(InvalidSeatCountException.class, () -> reservationService.create(memberDto, reservationInfoRequest));

    }

    @Test
    @DisplayName("버스 예약 실패: 최소 결제금액 미만")
    void create_fail_bad_payment() {

        ReservationInfoRequest reservationInfoRequest = ReservationInfoRequest.builder()
                .departureTime("12시30분")
                .arrivalTime("16시01분")
                .busNum(busNum)
                .departureDate(strDepartureDate)
                .departure("간성")
                .destination("동서울")
                .payment(0L)
                .seatNum(seatNum)
                .passengerType(new int[]{2, 0, 0})
                .busType("일반")
                .build();

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(busRepository.findByBusNumAndDepartureDate(any(Long.class), any(LocalDate.class))).thenReturn(Optional.of(bus));
        when(seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(any(Long.class), any(Bus.class))).thenReturn(Optional.empty());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        assertThrows(BadPaymentException.class, () -> reservationService.create(memberDto, reservationInfoRequest));

    }

    @Test
    @DisplayName("버스 예약 실패: 이미 예약된 자리")
    void create_fail_duplicated_seat() {

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(busRepository.findByBusNumAndDepartureDate(any(Long.class), any(LocalDate.class))).thenReturn(Optional.of(bus));
        when(seatRepository.findBySeatNumAndBusAndDeletedDateIsNull(any(Long.class), any(Bus.class))).thenReturn(Optional.of(seat));
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        assertThrows(DuplicateSeatException.class, () -> reservationService.create(memberDto, adultAndStandardReservationReq));

    }

    /* ========== 버스 예약 조회 ========== */
    @Test
    @DisplayName("버스 예약 조회: 전체 예약")
    void read_success_all() {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation());
        reservations.add(new Reservation());

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(reservationRepository.findByMemberAndDeletedDateIsNullOrderByCreatedDateDesc(member)).thenReturn(reservations);

        List<Reservation> result = reservationService.readAllReservation(memberDto);

        assertEquals(reservations, result);

    }

    @Test
    @DisplayName("버스 예약 조회: 결제 대기")
    void read_success_unpaid() {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation());
        reservations.add(new Reservation());

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(reservationRepository.findByMemberAndDeletedDateIsNullAndReservationStatusOrderByDepartureDateDescCreatedDateDesc(member, ReservationStatus.UNPAID)).thenReturn(reservations);

        List<Reservation> result = reservationService.readUnpaidReservation(memberDto);

        assertEquals(reservations, result);

    }

    @Test
    @DisplayName("버스 예약 조회: 결제 완료")
    void read_success_paid() {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation());
        reservations.add(new Reservation());

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(reservationRepository.findByMemberAndDeletedDateIsNullAndReservationStatusOrderByDepartureDateDescCreatedDateDesc(member, ReservationStatus.PAID)).thenReturn(reservations);

        List<Reservation> result = reservationService.readPaidReservation(memberDto);

        assertEquals(reservations, result);

    }

    @Test
    @DisplayName("버스 예약 조회: 만료")
    void read_success_expired() {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation());
        reservations.add(new Reservation());

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(reservationRepository.findByMemberAndDeletedDateIsNullAndReservationStatusOrderByDepartureDateDescCreatedDateDesc(member, ReservationStatus.EXPIRED)).thenReturn(reservations);

        List<Reservation> result = reservationService.readPastReservation(memberDto);

        assertEquals(reservations, result);

    }

    @Test
    @DisplayName("버스 예약 단건 리스트로 조회 성공")
    void read_detail_list_success() {

        reservation.setCreatedDate(createdDate);
        when(reservationRepository.findByIdAndDeletedDateIsNull(reservation.getId())).thenReturn(reservation);

        when(seatRepository.findById(seat.getId())).thenReturn(Optional.of(seat));

        ReservationDto result = reservationService.readByReservationId(reservationIds).get(0);

        assertEquals(reservationDto.getDeparture(), result.getDeparture());
        assertEquals(reservationDto.getDestination(), result.getDestination());
        assertEquals(reservationDto.getBusType(), result.getBusType());
        assertEquals(reservationDto.getBusNum(), result.getBusNum());
        assertEquals(reservationDto.getDepartureDate(), result.getDepartureDate());
        assertEquals(reservationDto.getDepartureTime(), result.getDepartureTime());
        assertEquals(reservationDto.getArrivalTime(), result.getArrivalTime());
        assertEquals(reservationDto.getPassengerType(), result.getPassengerType());
        assertEquals(reservationDto.getSeatNum(), result.getSeatNum());
        assertEquals(reservationDto.getCreatedDate(), result.getCreatedDate());
        assertEquals(reservationDto.getPayment(), result.getPayment());

    }

    @Test
    @DisplayName("버스 예약 단건 조회 성공")
    void read_detail_success() {

        reservation.setCreatedDate(createdDate);
        when(reservationRepository.findByIdAndDeletedDateIsNull(reservation.getId())).thenReturn(reservation);

        when(seatRepository.findById(seat.getId())).thenReturn(Optional.of(seat));

        ReservationDto result = reservationService.readReservationDetail(reservation.getId());

        assertEquals(reservation.getDeparture(), result.getDeparture());
        assertEquals(reservation.getDestination(), result.getDestination());
        assertEquals(reservation.getBusType(), result.getBusType());
        assertEquals(seat.getBus().getBusNum(), result.getBusNum());
        assertEquals(reservation.getDepartureDate(), result.getDepartureDate());
        assertEquals(reservation.getDepartureTime(), result.getDepartureTime());
        assertEquals(reservation.getArrivalTime(), result.getArrivalTime());
        assertEquals(reservation.getPassengerType(), result.getPassengerType());
        assertEquals(seat.getSeatNum(), result.getSeatNum());
        assertEquals(reservation.getCreatedDate().toLocalDate(), result.getCreatedDate());
        assertEquals(reservation.getPayment(), result.getPayment());

    }

    @Test
    @DisplayName("버스 예약 단건 조회 실패: 예약된 좌석 없음")
    void read_detail_fail_seat_not_found() {

        when(reservationRepository.findByIdAndDeletedDateIsNull(reservation.getId())).thenReturn(reservation);

        when(seatRepository.findById(seat.getId())).thenReturn(Optional.empty());

        assertThrows(SeatNotFoundException.class, () -> reservationService.readReservationDetail(reservation.getId()));

    }

    /* ========== 기타 ========== */
    @Test
    @DisplayName("가격 합계 구하기 성공")
    void get_payment_sum_success() {

        when(reservationRepository.findByIdAndDeletedDateIsNull(reservation.getId())).thenReturn(reservation);

        Long paymentSum = reservationService.getPaymentSum(reservationIds);
        assertEquals(reservation.getPayment(), paymentSum);

    }

    @Test
    @DisplayName("실 결제금 구하기 성공")
    void get_real_payment_sum_success() {

        when(reservationRepository.findByIdAndDeletedDateIsNull(reservation.getId())).thenReturn(reservation);

        Long realPaymentSum = reservationService.getRealPaymentSum(reservationIds);
        assertEquals(reservation.getPayment() - reservation.getUsedMileage(), realPaymentSum);

    }

    @Test
    @DisplayName("주문번호 문자열로 구하기 성공")
    void get_str_reservation_id_success() {

        Long[] reservationIds = {1L, 2L, 3L, 4L};

        String expected = "1, 2, 3, 4";
        String result = reservationService.getStrReservationIds(reservationIds);

        assertEquals(expected, result);

    }

    @Test
    @DisplayName("주문자 구하기 성공")
    void get_member_success() {

        when(reservationRepository.findByIdAndDeletedDateIsNull(reservationIds[0])).thenReturn(reservation);

        Member result = reservationService.getMember(reservationIds);

        assertEquals(member, result);

    }

    @Test
    @DisplayName("마일리지 구하기 성공")
    void get_mileage_success() {

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));

        Long mileage = reservationService.getMileage(memberDto);
        Long result = member.getMileage();

        assertEquals(mileage, result);

    }

}
