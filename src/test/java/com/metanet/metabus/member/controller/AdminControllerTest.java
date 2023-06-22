package com.metanet.metabus.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.metabus.bus.dto.BusPopularRoutesRequest;
import com.metanet.metabus.bus.dto.ReservationStatusRequest;
import com.metanet.metabus.bus.entity.ReservationStatus;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.dto.MemberInfoRequest;
import com.metanet.metabus.member.entity.Grade;
import com.metanet.metabus.member.entity.Role;
import com.metanet.metabus.member.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(AdminController.class)
@WithMockUser
class AdminControllerTest {

    @MockBean
    AdminService adminService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("운영자인경우")
    public void admin_ReturnsAdminPage() throws Exception {
        MockHttpSession session = new MockHttpSession();

        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.ADMIN, "123456789", Grade.ALPHA);

        session.setAttribute("loginMember", memberDto);


        List<ReservationStatusRequest> reservationList = new ArrayList<>();
        List<BusPopularRoutesRequest> busPopularRoutesRequestList = new ArrayList<>();
        List<MemberInfoRequest> memberInfoRequestList = new ArrayList<>();
        List<Long> countReservationUnpaidList = new ArrayList<>();
        List<Long> countReservationPaidList = new ArrayList<>();

        Mockito.when(adminService.findAllReservation()).thenReturn(reservationList);
        Mockito.when(adminService.findAllBus()).thenReturn(busPopularRoutesRequestList);
        Mockito.when(adminService.findAllMember()).thenReturn(memberInfoRequestList);
        Mockito.when(adminService.countReservation(ReservationStatus.UNPAID)).thenReturn(countReservationUnpaidList);
        Mockito.when(adminService.countReservation(ReservationStatus.PAID)).thenReturn(countReservationPaidList);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mypage/admin"))
                .andExpect(MockMvcResultMatchers.model().attribute("reservationList", reservationList))
                .andExpect(MockMvcResultMatchers.model().attribute("busList", busPopularRoutesRequestList))
                .andExpect(MockMvcResultMatchers.model().attribute("memberList", memberInfoRequestList))
                .andExpect(MockMvcResultMatchers.model().attribute("countUnpaidList", countReservationUnpaidList))
                .andExpect(MockMvcResultMatchers.model().attribute("countPaidList", countReservationPaidList))
                .andExpect(MockMvcResultMatchers.model().attribute("memberDto", memberDto));
    }

    @Test
    @DisplayName("로그인안한경우")
    public void WhenNotLoggedIn() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/member/login"));
    }

    @Test
    @DisplayName("운영자가아닌경우")
    public void WhenNotAdmin() throws Exception {
        MockHttpSession session = new MockHttpSession();

        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                Role.USER, "123456789", Grade.ALPHA);

        session.setAttribute("loginMember", memberDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/error/401"));
    }

}