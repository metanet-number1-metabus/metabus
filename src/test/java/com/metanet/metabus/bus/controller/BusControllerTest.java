package com.metanet.metabus.bus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.metabus.bus.dto.ReceiptResponse;
import com.metanet.metabus.bus.dto.ReservationDto;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.service.PaymentService;
import com.metanet.metabus.bus.service.ReservationService;
import com.metanet.metabus.member.controller.SessionConst;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Grade;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BusController.class)
@WithMockUser
class BusControllerTest {

    @MockBean
    ReservationService reservationService;

    @MockBean
    PaymentService paymentService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    @DisplayName("버스 시간표 GET 성공")
    void get_searchBus() throws Exception {

        String departureHome = "간성";
        String destinationHome = "동서울";
        String departureDate = "06/15";
        String roundTrip = "on";

        mockMvc.perform(get("/bus/timetable")
                        .param("departurehome", departureHome)
                        .param("destinationhome", destinationHome)
                        .param("departuredate", departureDate)
                        .param("roundtrip", roundTrip))
                .andExpect(status().isOk())
                .andExpect(view().name("bus/bus-time-table"))
                .andExpect(model().attribute("departureHome", departureHome))
                .andExpect(model().attribute("destinationHome", destinationHome))
                .andExpect(model().attribute("departureDate", departureDate))
                .andExpect(model().attribute("roundTrip", roundTrip));
    }

    @Test
    @DisplayName("버스 시간표 GET 성공(1) - 예매 탭이 null 일 때")
    void get_readReservation_success() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        List<Reservation> reservationList = new ArrayList<>();

        given(reservationService.readAllReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readUnpaidReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readPaidReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readPastReservation(any(MemberDto.class))).willReturn(reservationList);


        mockMvc.perform(get("/bus/reservation")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("bus/bus-reservation-table"))
                .andExpect(model().attribute("ReservationList", reservationList))
                .andExpect(model().attribute("checkButton", "전체"))
                .andExpect(model().attribute("memberDto", memberDto));
    }

    @Test
    @DisplayName("버스 시간표 GET 성공(2) - 예매 탭이 전체 일 때")
    void get_readReservation_success2() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        List<Reservation> reservationList = new ArrayList<>();

        given(reservationService.readAllReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readUnpaidReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readPaidReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readPastReservation(any(MemberDto.class))).willReturn(reservationList);

        mockMvc.perform(get("/bus/reservation")
                        .param("specialValue", "전체")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("bus/bus-reservation-table"))
                .andExpect(model().attribute("ReservationList", reservationList))
                .andExpect(model().attribute("checkButton", "전체"))
                .andExpect(model().attribute("memberDto", memberDto));
    }

    @Test
    @DisplayName("버스 시간표 GET 성공(3) - 예매 탭이 결제대기 일 때")
    void get_readReservation_success3() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        List<Reservation> reservationList = new ArrayList<>();

        given(reservationService.readAllReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readUnpaidReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readPaidReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readPastReservation(any(MemberDto.class))).willReturn(reservationList);

        mockMvc.perform(get("/bus/reservation")
                        .param("specialValue", "결제대기")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("bus/bus-reservation-table"))
                .andExpect(model().attribute("ReservationList", reservationList))
                .andExpect(model().attribute("checkButton", "결제대기"))
                .andExpect(model().attribute("memberDto", memberDto));
    }

    @Test
    @DisplayName("버스 시간표 GET 성공(4) - 예매 탭이 결제완료 일 때")
    void get_readReservation_success4() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        List<Reservation> reservationList = new ArrayList<>();

        given(reservationService.readAllReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readUnpaidReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readPaidReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readPastReservation(any(MemberDto.class))).willReturn(reservationList);

        mockMvc.perform(get("/bus/reservation")
                        .param("specialValue", "결제완료")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("bus/bus-reservation-table"))
                .andExpect(model().attribute("ReservationList", reservationList))
                .andExpect(model().attribute("checkButton", "결제완료"))
                .andExpect(model().attribute("memberDto", memberDto));
    }

    @Test
    @DisplayName("버스 시간표 GET 성공(5) - 예매 탭이 만료된 승차권 일 때")
    void get_readReservation_success5() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        List<Reservation> reservationList = new ArrayList<>();

        given(reservationService.readAllReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readUnpaidReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readPaidReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readPastReservation(any(MemberDto.class))).willReturn(reservationList);

        mockMvc.perform(get("/bus/reservation")
                        .param("specialValue", "만료된 승차권")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("bus/bus-reservation-table"))
                .andExpect(model().attribute("ReservationList", reservationList))
                .andExpect(model().attribute("checkButton", "만료된 승차권"))
                .andExpect(model().attribute("memberDto", memberDto));
    }

    @Test
    @DisplayName("버스 시간표 GET 성공(6) - 예매 탭이 그 외 일 때")
    void get_readReservation_success6() throws Exception {
        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        List<Reservation> reservationList = new ArrayList<>();

        given(reservationService.readAllReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readUnpaidReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readPaidReservation(any(MemberDto.class))).willReturn(reservationList);
        given(reservationService.readPastReservation(any(MemberDto.class))).willReturn(reservationList);

        mockMvc.perform(get("/bus/reservation")
                        .param("specialValue", "그 외")
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("bus/bus-reservation-table"))
                .andExpect(model().attribute("ReservationList", reservationList))
                .andExpect(model().attribute("checkButton", "전체"))
                .andExpect(model().attribute("memberDto", memberDto));
    }

    @Test
    @DisplayName("예매 내역 결제 POST 성공")
    void post_getPaymentList_success() throws Exception {

        ReservationDto reservationDto = ReservationDto.builder()
                .departure("간성")
                .destination("동서울")
                .busType("일반")
                .busNum(10000L)
                .departureDate(LocalDate.now().plusDays(30))
                .departureTime("12:30")
                .arrivalTime("16:01")
                .passengerType("성인")
                .seatNum(1L)
                .createdDate(LocalDateTime.now().toLocalDate())
                .payment(21100L)
                .build();

        Long[] reservationIds = {1L};

        List<ReservationDto> reservationList = new ArrayList<>();
        reservationList.add(reservationDto);

        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);

        given(reservationService.readByReservationId(reservationIds)).willReturn(reservationList);
        given(reservationService.getPaymentSum(reservationIds)).willReturn(10000L);
        given(reservationService.getStrReservationIds(reservationIds)).willReturn("1");
        given(reservationService.getMember(reservationIds)).willReturn(member);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(post("/bus/payment")
                        .param("data", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("bus/bus-payment"))
                .andExpect(model().attribute("reservationListSize", reservationList.size()))
                .andExpect(model().attribute("reservationList", reservationList))
                .andExpect(model().attribute("paymentSum", 10000L))
                .andExpect(model().attribute("strReservationIds", "1"))
                .andExpect(model().attribute("member", member));
    }

    @Test
    @DisplayName("결제 내역 취소 POST 성공")
    void post_getCancelList_success() throws Exception {

        ReservationDto reservationDto = ReservationDto.builder()
                .departure("간성")
                .destination("동서울")
                .busType("일반")
                .busNum(10000L)
                .departureDate(LocalDate.now().plusDays(30))
                .departureTime("12:30")
                .arrivalTime("16:01")
                .passengerType("성인")
                .seatNum(1L)
                .createdDate(LocalDateTime.now().toLocalDate())
                .payment(21100L)
                .build();

        Long[] reservationIds = {1L};

        List<ReservationDto> reservationList = new ArrayList<>();
        reservationList.add(reservationDto);

        given(reservationService.readByReservationId(reservationIds)).willReturn(reservationList);
        given(reservationService.getRealPaymentSum(reservationIds)).willReturn(10000L);
        given(reservationService.getStrReservationIds(reservationIds)).willReturn("1");
        given(paymentService.getImpUid(reservationIds)).willReturn("imp_111111111111");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(post("/bus/cancel")
                        .param("data", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("bus/bus-cancel"))
                .andExpect(model().attribute("reservationList", reservationList))
                .andExpect(model().attribute("paymentSum", 10000L))
                .andExpect(model().attribute("strReservationIds", "1"))
                .andExpect(model().attribute("impUid", "imp_111111111111"));
    }

    @Test
    @DisplayName("영수증 GET 성공")
    void get_receipt() throws Exception {

        String impUid = "imp_178492282208";

        ReceiptResponse receiptResponse = ReceiptResponse.builder()
                .applyNum("33423394")
                .memberEmail("user@test.com")
                .memberName("user")
                .memberPhoneNum("010-1234-5678")
                .cardName("신한카드")
                .cardNum("449914*********0")
                .impUid(impUid)
                .merchantName("메타버스")
                .payment(21100L)
                .payMethod("card")
                .usedMileage(1000L)
                .build();

        given(paymentService.makeReceipt(impUid)).willReturn(receiptResponse);

        mockMvc.perform(get("/pay/receipt/{impUid}", impUid))
                .andExpect(status().isOk())
                .andExpect(view().name("bus/receipt"))
                .andExpect(model().attribute("receiptResponse", receiptResponse));
    }
}