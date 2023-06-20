package com.metanet.metabus.bus.controller;

import com.metanet.metabus.bus.dto.ReceiptResponse;
import com.metanet.metabus.bus.dto.ReservationDto;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.service.PaymentService;
import com.metanet.metabus.bus.service.ReservationService;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BusController {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    @GetMapping("/bus/timetable")
    public String searchBus(
            @RequestParam("departurehome") String departureHome,
            @RequestParam("destinationhome") String destinationHome,
            @RequestParam("departuredate") String departureDate,
            @RequestParam(value = "roundtrip", defaultValue = "off") String roundTrip, HttpSession session, Model model
    ) {

        if ((departureDate.equals("")) || (destinationHome.equals("")) || (departureHome.equals(""))) {
            return "error/404";
        }

        model.addAttribute("departureHome", departureHome);
        model.addAttribute("destinationHome", destinationHome);
        model.addAttribute("departureDate", departureDate);
        model.addAttribute("roundTrip", roundTrip);

        MemberDto memberDto = (MemberDto) session.getAttribute("loginMember");
        model.addAttribute("memberDto", memberDto);

        return "bus/bus-time-table";
    }

    @GetMapping("/bus/reservation")
    public String readReservation(HttpServletRequest request, HttpSession session, Model model) {
        String specialValue = request.getParameter("specialValue");

        MemberDto memberDto = (MemberDto) session.getAttribute("loginMember");

        List<Reservation> allReservationList = reservationService.readAllReservation(memberDto);
        List<Reservation> unpaidReservationList = reservationService.readUnpaidReservation(memberDto);
        List<Reservation> paidReservationList = reservationService.readPaidReservation(memberDto);
        List<Reservation> pastReservationList = reservationService.readPastReservation(memberDto);

        if (specialValue == null) {
            model.addAttribute("ReservationList", allReservationList);
            model.addAttribute("checkButton", "전체");
        } else if (specialValue.equals("전체")) {
            model.addAttribute("ReservationList", allReservationList);
            model.addAttribute("checkButton", "전체");
        } else if (specialValue.equals("결제대기")) {
            model.addAttribute("ReservationList", unpaidReservationList);
            model.addAttribute("checkButton", "결제대기");
        } else if (specialValue.equals("결제완료")) {
            model.addAttribute("ReservationList", paidReservationList);
            model.addAttribute("checkButton", "결제완료");
        } else if (specialValue.equals("만료된 승차권")) {
            model.addAttribute("ReservationList", pastReservationList);
            model.addAttribute("checkButton", "만료된 승차권");
        } else {
            model.addAttribute("ReservationList", allReservationList);
            model.addAttribute("checkButton", "전체");
        }
        model.addAttribute("memberDto", memberDto);

        return "bus/bus-reservation-table";
    }

    @PostMapping("/bus/payment")
    public String getPaymentList(@RequestParam("data") Long[] reservationIds, HttpSession session, Model model) {

        MemberDto memberDto = (MemberDto) session.getAttribute("loginMember");

        List<ReservationDto> reservationList = reservationService.readByReservationId(reservationIds);
        Long paymentSum = reservationService.getPaymentSum(reservationIds);
        String strReservationIds = reservationService.getStrReservationIds(reservationIds);
        Member member = reservationService.getMember(reservationIds);
        Long mileage = reservationService.getMileage(memberDto);

        model.addAttribute("memberDto", memberDto);
        model.addAttribute("reservationListSize", reservationList.size());
        model.addAttribute("reservationList", reservationList);
        model.addAttribute("paymentSum", paymentSum);
        model.addAttribute("strReservationIds", strReservationIds);
        model.addAttribute("member", member);
        model.addAttribute("mileage", mileage);

        return "bus/bus-payment";
    }

    @PostMapping("/bus/cancel")
    public String getCancelList(@RequestParam("data") Long[] reservationIds, HttpSession session, Model model) {

        MemberDto memberDto = (MemberDto) session.getAttribute("loginMember");

        List<ReservationDto> reservationList = reservationService.readByReservationId(reservationIds);
        Long paymentSum = reservationService.getPaymentSum(reservationIds);
        Long usedMileageSum = reservationService.getUsedMileageSum(reservationIds);
        String strReservationIds = reservationService.getStrReservationIds(reservationIds);
        String impUid = paymentService.getImpUid(reservationIds);

        model.addAttribute("memberDto", memberDto);
        model.addAttribute("reservationListSize", reservationList.size());
        model.addAttribute("reservationList", reservationList);
        model.addAttribute("paymentSum", paymentSum);
        model.addAttribute("usedMileageSum", usedMileageSum);
        model.addAttribute("strReservationIds", strReservationIds);
        model.addAttribute("impUid", impUid);

        return "bus/bus-cancel";
    }

    @GetMapping("/pay/receipt/{impUid}")
    public String makeReceipt(@PathVariable String impUid, Model model) {

        ReceiptResponse receiptResponse = paymentService.makeReceipt(impUid);

        model.addAttribute("receiptResponse", receiptResponse);

        return "bus/receipt";
    }

}
