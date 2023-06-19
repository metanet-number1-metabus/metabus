package com.metanet.metabus.bus.service;

import com.metanet.metabus.bus.dto.PayRequest;
import com.metanet.metabus.bus.dto.ReceiptResponse;
import com.metanet.metabus.bus.entity.*;
import com.metanet.metabus.bus.repository.PaymentRepository;
import com.metanet.metabus.bus.repository.ReservationRepository;
import com.metanet.metabus.bus.repository.SeatRepository;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.repository.MemberRepository;
import com.metanet.metabus.mileage.repository.MileageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    private final ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);
    private final SeatRepository seatRepository = Mockito.mock(SeatRepository.class);
    private final MemberRepository memberRepository = Mockito.mock(MemberRepository.class);
    private final PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
    private final MileageRepository mileageRepository = Mockito.mock(MileageRepository.class);
    private final PaymentService paymentService = new PaymentService(reservationRepository, seatRepository, memberRepository, paymentRepository, mileageRepository);

    private final Long busNum = 10000L;
    private final LocalDate departureDate = LocalDate.now().plusDays(30);

    private final Member member = Member.builder()
            .id(1L)
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

    private final PayRequest payRequest = PayRequest.builder()
            .applyNum("33423394")
            .memberId(member.getId())
            .cardName("신한카드")
            .cardNum("449914*********0")
            .impUid("imp_178492282208")
            .merchantName("메타버스")
            .payment(21100L)
            .payMethod("card")
            .reservationIds("1")
            .usedMileage(0L)
            .build();
    private final Payment payment = Payment.builder()
            .applyNum("33423394")
            .member(member)
            .cardName("신한카드")
            .cardNum("449914*********0")
            .impUid("imp_178492282208")
            .merchantName("메타버스")
            .payment(21100L)
            .payMethod("card")
            .usedMileage(0L)
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

    @Test
    @DisplayName("결제 완료 성공")
    void complete_payment_success() {
        when(memberRepository.findById(payRequest.getMemberId())).thenReturn(Optional.of(member));
        when(reservationRepository.findByIdAndDeletedDateIsNull(anyLong())).thenReturn(reservation);

        paymentService.completePayment(payRequest);

    }

    @Test
    @DisplayName("결제 완료 성공: 마일리지 사용 & 예약 한 개")
    void complete_payment_success_with_mileage() {

        PayRequest payRequest = PayRequest.builder()
                .applyNum("33423394")
                .memberId(member.getId())
                .cardName("신한카드")
                .cardNum("449914*********0")
                .impUid("imp_178492282208")
                .merchantName("메타버스")
                .payment(21100L)
                .payMethod("card")
                .reservationIds("1")
                .usedMileage(1000L)
                .build();

        when(memberRepository.findById(payRequest.getMemberId())).thenReturn(Optional.of(member));
        when(reservationRepository.findByIdAndDeletedDateIsNull(anyLong())).thenReturn(reservation);

        paymentService.completePayment(payRequest);

    }

    @Test
    @DisplayName("결제 완료 성공: 마일리지 사용 & 예약 한 개 이상")
    void complete_payments_success_with_mileage() {

        PayRequest payRequest = PayRequest.builder()
                .applyNum("33423394")
                .memberId(member.getId())
                .cardName("신한카드")
                .cardNum("449914*********0")
                .impUid("imp_178492282208")
                .merchantName("메타버스")
                .payment(21100L)
                .payMethod("card")
                .reservationIds("1, 2")
                .usedMileage(1000L)
                .build();

        when(memberRepository.findById(payRequest.getMemberId())).thenReturn(Optional.of(member));
        when(reservationRepository.findByIdAndDeletedDateIsNull(anyLong())).thenReturn(reservation);

        paymentService.completePayment(payRequest);

    }

    @Test
    @DisplayName("결제 취소 성공")
    void payment_cancel_db_success() {
        String reservationIds = "1";

        when(reservationRepository.findByIdAndDeletedDateIsNull(reservation.getId())).thenReturn(reservation);
        when(seatRepository.findById(reservation.getSeatId().getId())).thenReturn(Optional.of(seat));

        paymentService.paymentCancelDb(reservationIds);
    }

    @Test
    @DisplayName("주문 번호 얻기 성공")
    void get_impUid_success() {
        Long[] reservationIds = new Long[]{1L};

        when(reservationRepository.findByIdAndDeletedDateIsNull(reservationIds[0])).thenReturn(reservation);
        String expected = reservation.getImpUid();

        String result = paymentService.getImpUid(reservationIds);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("영수증 만들기 성공")
    void make_receipt_success() {
        ReceiptResponse receiptResponse = ReceiptResponse.builder()
                .applyNum("33423394")
                .memberEmail(member.getEmail())
                .memberName(member.getName())
                .memberPhoneNum(member.getPhoneNum())
                .cardName("신한카드")
                .cardNum("449914*********0")
                .impUid("imp_178492282208")
                .merchantName("메타버스")
                .payment(21100L)
                .payMethod("card")
                .usedMileage(0L)
                .build();

        when(paymentRepository.findByImpUidAndDeletedDateIsNull(payment.getImpUid())).thenReturn(payment);

        ReceiptResponse result = paymentService.makeReceipt(payment.getImpUid());

        assertEquals(receiptResponse.getApplyNum(), result.getApplyNum());
        assertEquals(receiptResponse.getMemberEmail(), result.getMemberEmail());
        assertEquals(receiptResponse.getMemberName(), result.getMemberName());
        assertEquals(receiptResponse.getMemberPhoneNum(), result.getMemberPhoneNum());
        assertEquals(receiptResponse.getCardName(), result.getCardName());
        assertEquals(receiptResponse.getCardNum(), result.getCardNum());
        assertEquals(receiptResponse.getImpUid(), result.getImpUid());
        assertEquals(receiptResponse.getMerchantName(), result.getMerchantName());
        assertEquals(receiptResponse.getPayment(), result.getPayment());
        assertEquals(receiptResponse.getPayMethod(), result.getPayMethod());
        assertEquals(receiptResponse.getUsedMileage(), result.getUsedMileage());
    }

}
