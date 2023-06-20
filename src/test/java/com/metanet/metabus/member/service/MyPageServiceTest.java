package com.metanet.metabus.member.service;

import com.metanet.metabus.bus.entity.Bus;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.ReservationStatus;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Grade;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.repository.MemberRepository;
import com.metanet.metabus.member.repository.MyPageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MyPageServiceTest {

    private final MyPageRepository myPageRepository = mock(MyPageRepository .class);
    private final MemberRepository memberRepository = mock(MemberRepository .class);

    private final MyPageService myPageService = new MyPageService(myPageRepository, memberRepository);

    @Test
    @DisplayName("티켓수 체트")
    public void testSelectTickets() {

        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.ADMIN, "123456789", Grade.ALPHA);

        Member member = new Member(1L,"나","123456789","",
                9999L,com.metanet.metabus.member.entity.Role.ADMIN,"123456789",Grade.ALPHA);

        Bus bus = new Bus(1L,1L, LocalDate.now());
        Seat seat = new Seat(1L,1L,bus);
        Reservation reservation = new Reservation(1L,member,"출발지","도착지",
                "도착시간","도착시간",LocalDate.now(),1L,seat,"도착지",
                ReservationStatus.PAID,"도착지","도착지",1L,1L);


        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation);


        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));


        when(myPageRepository.findByMemberAndDeletedDateIsNullAndReservationStatus(member, ReservationStatus.PAID))
                .thenReturn(reservationList);


        List<Reservation> result = myPageService.selectTickets(memberDto);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(ReservationStatus.PAID, result.get(0).getReservationStatus());
    }

    @Test
    @DisplayName("날짜 생성")
    public void testGetCreatedDate() {

        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.ADMIN, "123456789", Grade.ALPHA);

        Member member = new Member(1L,"나","123456789","",
                9999L,com.metanet.metabus.member.entity.Role.ADMIN,"123456789",Grade.ALPHA);
        member.setCreatedDate(LocalDate.now().atStartOfDay());
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        LocalDate createdDate = myPageService.getCreatedDate(memberDto);

        Assertions.assertEquals(LocalDate.now(), createdDate);
    }

    @Test
    @DisplayName("점수 가져오기")
    public void testSelectGrade() {

        Long memberId = 1L;

        String grade = "최고";

        when(myPageRepository.myGrede(memberId)).thenReturn(grade);

        // 메서드 호출
        String result = myPageService.selectGrade(memberId);

        // 결과 검증
        Assertions.assertEquals(grade, result);
    }

    @Test
    @DisplayName("마일리지 가져오기")
    public void testSelectMileage() {

        Long memberId = 1L;

        Long mileage = 9999L;

        when(myPageRepository.myMileage(memberId)).thenReturn(mileage);

        Long result = myPageService.selectMileage(memberId);

        Assertions.assertEquals(mileage, result);
    }

}