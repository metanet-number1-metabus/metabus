package com.metanet.metabus.mileage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.metabus.bus.dto.ReservationResponse;
import com.metanet.metabus.bus.service.ReservationService;
import com.metanet.metabus.member.controller.SessionConst;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Grade;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.entity.Role;
import com.metanet.metabus.mileage.dto.MileageDto;
import com.metanet.metabus.mileage.service.MileageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MileageController.class)
@WithMockUser
class MileageControllerTest {

    @MockBean
    MileageService mileageService;

    @MockBean
    ReservationService reservationService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("마일리지 정보 GET 성공")
    void get_mileage_success() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        List<MileageDto> mileageList = new ArrayList<>();
        List<ReservationResponse> numberOfReservations = new ArrayList<>();

        given(mileageService.findAllMileage(any(MemberDto.class))).willReturn(0L);
        given(mileageService.findUsedMileage(any(MemberDto.class))).willReturn(0L);
        given(mileageService.findMileage(any(MemberDto.class))).willReturn(0L);
        given(mileageService.findMileageByMember(any(MemberDto.class))).willReturn(mileageList);
        given(mileageService.getMember(any(MemberDto.class))).willReturn(member);
        given(reservationService.readPastReservation(any(MemberDto.class))).willReturn(numberOfReservations);

        mockMvc.perform(get("/mileage")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/mileage-table"))
                .andExpect(model().attribute("allMileage", 0L))
                .andExpect(model().attribute("usedMileage", 0L))
                .andExpect(model().attribute("mileage", 0L))
                .andExpect(model().attribute("mileageList", mileageList))
                .andExpect(model().attribute("memberGrade", member.getGrade()))
                .andExpect(model().attribute("numberOfReservations", numberOfReservations.size()))
                .andExpect(model().attribute("memberDto", memberDto));

    }

}
