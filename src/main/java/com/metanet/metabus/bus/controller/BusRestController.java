package com.metanet.metabus.bus.controller;

import com.metanet.metabus.bus.dto.CancelDto;
import com.metanet.metabus.bus.dto.PayDto;
import com.metanet.metabus.bus.dto.ReservationDto;
import com.metanet.metabus.bus.dto.ReservationInfoRequest;
import com.metanet.metabus.bus.service.PaymentService;
import com.metanet.metabus.bus.service.ReservationService;
import com.metanet.metabus.bus.service.SeatService;
import com.metanet.metabus.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BusRestController {

    private final SeatService seatService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    @GetMapping("/read/{busNum}/{departureDate}")
    public List<Long> read(@PathVariable Long busNum, @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") LocalDate departureDate) {
        return seatService.read(busNum, departureDate);
    }

    @PostMapping("/bus/reservation")
    public List<Long> makeReservation(HttpSession session, @RequestBody ReservationInfoRequest reservationInfoRequest) {

        MemberDto memberDto = (MemberDto) session.getAttribute("loginMember");
        return reservationService.create(memberDto, reservationInfoRequest);
    }

    @GetMapping("/read/detail/{reservationId}")
    public ReservationDto readReservationDetail(@PathVariable Long reservationId) {
        return reservationService.readReservationDetail(reservationId);
    }

    @PostMapping("/bus/payment/complete")
    public void completePayment(@RequestBody PayDto payDto) {
        String merchantUid = payDto.getMerchantUid();
        String reservationIds = payDto.getReservationIds();
        reservationService.completePayment(merchantUid, reservationIds);
    }

    @PostMapping("/bus/payment/cancel")
    public void paymentCancel(@RequestBody CancelDto cancelDto) throws Exception {

        String accessToken = paymentService.getToken();
        String merchantUid = cancelDto.getMerchantUid();
        String reservationIds = cancelDto.getReservationIds();
        String amount = cancelDto.getPaymentSum();

        paymentService.paymentCancel(accessToken, merchantUid, amount);

        reservationService.paymentCancel(reservationIds);
    }

}
