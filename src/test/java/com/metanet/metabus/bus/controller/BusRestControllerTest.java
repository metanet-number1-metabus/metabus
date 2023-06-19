package com.metanet.metabus.bus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.metabus.bus.dto.*;
import com.metanet.metabus.bus.service.PaymentService;
import com.metanet.metabus.bus.service.ReservationService;
import com.metanet.metabus.bus.service.SeatService;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BusRestController.class)
@WithMockUser
class BusRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SeatService seatService;

    @MockBean
    ReservationService reservationService;

    @MockBean
    PaymentService paymentService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("버스 시간표 GET 성공")
    void read_bus_time_table_success() throws Exception {
        Long busNum = 10000L;
        LocalDate departureDate = LocalDate.now().plusDays(30);

        List<Long> seatNum = new ArrayList<>();
        seatNum.add(1L);
        seatNum.add(2L);

        when(seatService.read(busNum, departureDate)).thenReturn(seatNum);

        mockMvc.perform(get("/read/{busNum}/{departureDate}", busNum, departureDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1L))
                .andExpect(jsonPath("$[1]").value(2L))
                .andReturn();
    }

    @Test
    @DisplayName("버스 예약 POST 성공")
    void make_reservation_success() throws Exception {

        MemberDto memberDto = new MemberDto(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        Long busNum = 10000L;
        LocalDate departureDate = LocalDate.now().plusDays(30);
        String strDepartureDate = departureDate.format(DateTimeFormatter.ofPattern("MM/dd"));
        Long[] seatNum = new Long[]{1L, 2L};

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

        List<Long> reservationIds = new ArrayList<>();
        reservationIds.add(1L);
        reservationIds.add(2L);

        when(reservationService.create(any(MemberDto.class), any(ReservationInfoRequest.class))).thenReturn(reservationIds);

        mockMvc.perform(post("/bus/reservation")
                        .session(mockHttpSession)
                        .content(objectMapper.writeValueAsString(reservationInfoRequest))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1L))
                .andExpect(jsonPath("$[1]").value(2L))
                .andReturn();
    }

    @Test
    @DisplayName("예약 세부 정보 GET 성공")
    void read_reservation_detail_success() throws Exception {

        Long reservationId = 1L;

        Long busNum = 10000L;
        LocalDate departureDate = LocalDate.now().plusDays(30);
        Long[] seatNum = new Long[]{1L, 2L};
        LocalDateTime createdDate = LocalDateTime.now();

        ReservationDto reservationDto = ReservationDto.builder()
                .departure("간성")
                .destination("동서울")
                .busType("일반")
                .busNum(busNum)
                .departureDate(departureDate)
                .departureTime("12:30")
                .arrivalTime("16:01")
                .passengerType("성인")
                .seatNum(seatNum[0])
                .createdDate(createdDate.toLocalDate())
                .payment(21100L)
                .build();

        when(reservationService.readReservationDetail(reservationId)).thenReturn(reservationDto);

        mockMvc.perform(get("/read/detail/{reservationId}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departure").value(reservationDto.getDeparture()))
                .andExpect(jsonPath("$.destination").value(reservationDto.getDestination()))
                .andExpect(jsonPath("$.busType").value(reservationDto.getBusType()))
                .andExpect(jsonPath("$.busNum").value(reservationDto.getBusNum()))
                .andExpect(jsonPath("$.departureDate").value(reservationDto.getDepartureDate().toString()))
                .andExpect(jsonPath("$.departureTime").value(reservationDto.getDepartureTime()))
                .andExpect(jsonPath("$.arrivalTime").value(reservationDto.getArrivalTime()))
                .andExpect(jsonPath("$.passengerType").value(reservationDto.getPassengerType()))
                .andExpect(jsonPath("$.seatNum").value(reservationDto.getSeatNum()))
                .andExpect(jsonPath("$.createdDate").value(reservationDto.getCreatedDate().toString()))
                .andExpect(jsonPath("$.payment").value(reservationDto.getPayment()))
                .andReturn();

    }

    @Test
    @DisplayName("결제 완료 POST 성공")
    void complete_payment_success() throws Exception {

        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);

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

        mockMvc.perform(post("/bus/payment/complete")
                        .content(objectMapper.writeValueAsString(payRequest))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("결제 취소 POST 성공")
    void cancel_payment_success() throws Exception {
        CancelDto cancelDto = new CancelDto("imp_178492282208", "1", "21100");

        String accessToken = "testAccessToken";
        String impUid = cancelDto.getImpUid();
        String reservationIds = cancelDto.getReservationIds();
        String amount = cancelDto.getPaymentSum();

        doNothing().when(paymentService).paymentCancelApi(accessToken, impUid, amount);
        doNothing().when(paymentService).paymentCancelDb(reservationIds);

        mockMvc.perform(post("/bus/payment/cancel")
                        .content(objectMapper.writeValueAsString(cancelDto))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("영수증 정보 리턴 GET 성공")
    void make_receipt_success() throws Exception {
        String impUid = "imp_178492282208";
        Member member = new Member(0L, "test", "12345678", "test@test.com", 0L, Role.USER, "010-0000-0000", Grade.ALPHA);

        ReceiptResponse receiptResponse = ReceiptResponse.builder()
                .applyNum("33423394")
                .memberEmail(member.getEmail())
                .memberName(member.getName())
                .memberPhoneNum(member.getPhoneNum())
                .cardName("신한카드")
                .cardNum("449914*********0")
                .impUid(impUid)
                .merchantName("메타버스")
                .payment(21100L)
                .payMethod("card")
                .usedMileage(0L)
                .build();

        when(paymentService.makeReceipt(impUid)).thenReturn(receiptResponse);

        mockMvc.perform(get("/bus/receipt/{impUid}", impUid))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.applyNum").value(receiptResponse.getApplyNum()))
                .andExpect(jsonPath("$.memberEmail").value(receiptResponse.getMemberEmail()))
                .andExpect(jsonPath("$.memberName").value(receiptResponse.getMemberName()))
                .andExpect(jsonPath("$.memberPhoneNum").value(receiptResponse.getMemberPhoneNum()))
                .andExpect(jsonPath("$.cardName").value(receiptResponse.getCardName()))
                .andExpect(jsonPath("$.cardNum").value(receiptResponse.getCardNum()))
                .andExpect(jsonPath("$.impUid").value(receiptResponse.getImpUid()))
                .andExpect(jsonPath("$.merchantName").value(receiptResponse.getMerchantName()))
                .andExpect(jsonPath("$.payment").value(receiptResponse.getPayment()))
                .andExpect(jsonPath("$.payMethod").value(receiptResponse.getPayMethod()))
                .andExpect(jsonPath("$.usedMileage").value(receiptResponse.getUsedMileage()))
                .andReturn();

    }


}
