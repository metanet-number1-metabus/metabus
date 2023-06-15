package com.metanet.metabus.mileage.controller;

import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.service.ReservationService;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Grade;
import com.metanet.metabus.mileage.dto.MileageDto;
import com.metanet.metabus.mileage.service.MileageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MileageController {

    private final MileageService mileageService;
    private final ReservationService reservationService;

    @GetMapping("/mileage")
    public String searchBus(HttpSession session, Model model) {
        MemberDto memberDto = (MemberDto) session.getAttribute("loginMember");

        Long allMileage = mileageService.findAllMileage(memberDto);
        Long usedMileage = mileageService.findUsedMileage(memberDto);
        Long mileage = mileageService.findMileage(memberDto);
        List<MileageDto> mileageList = mileageService.findMileageByMember(memberDto);
        Grade memberGrade = mileageService.getMember(memberDto).getGrade();
        List<Reservation> reservationList = reservationService.readPastReservation(memberDto);
        int numberOfReservations = reservationList.size();

        model.addAttribute("allMileage", allMileage);
        model.addAttribute("usedMileage", usedMileage);
        model.addAttribute("mileage", mileage);
        model.addAttribute("mileageList", mileageList);
        model.addAttribute("memberGrade", memberGrade);
        model.addAttribute("numberOfReservations", numberOfReservations);
        model.addAttribute("memberDto", memberDto);

        return "mypage/mileage-table";
    }

}