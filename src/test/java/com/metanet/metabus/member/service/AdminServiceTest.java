package com.metanet.metabus.member.service;

import com.metanet.metabus.board.repository.BoardRepository;
import com.metanet.metabus.board.service.BoardService;
import com.metanet.metabus.bus.dto.BusPopularRoutesRequest;
import com.metanet.metabus.bus.dto.ReservationStatusRequest;
import com.metanet.metabus.bus.entity.Bus;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.ReservationStatus;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.bus.repository.ReservationRepository;
import com.metanet.metabus.bus.repository.SeatRepository;
import com.metanet.metabus.member.dto.MemberInfoRequest;
import com.metanet.metabus.member.entity.Grade;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.entity.Role;
import com.metanet.metabus.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminServiceTest {

    private final ReservationRepository reservationRepository = mock(ReservationRepository.class);
    private final SeatRepository seatRepository = mock(SeatRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final AdminService adminService = new AdminService(reservationRepository,seatRepository,memberRepository);

    @Test
    @DisplayName("버스 찾기시간지나감")
    public void findAllBus_ReturnsBusPopularRoutesRequestList() {

        Bus bus1 = new Bus(1L,1L, LocalDate.now().minusDays(2));

        Seat seat1 = new Seat(1L,1L,bus1);

        List<Seat> seatList = new ArrayList<>();
        seatList.add(seat1);

        when(seatRepository.findAll()).thenReturn(seatList);


        List<BusPopularRoutesRequest> result = adminService.findAllBus();

        assertEquals(0, result.size());


    }

    @Test
    @DisplayName("버스 찾기시간안지나감")
    public void findAllBus_ReturnsBusPopularRoutesRequestList_after() {

        Bus bus1 = Bus.of(1L, LocalDate.now().plusDays(2));

        Seat seat1 = new Seat(1L,1L,bus1);
        List<Seat> seatList = new ArrayList<>();
        seatList.add(seat1);




        when(seatRepository.findAll()).thenReturn(seatList);

        List<BusPopularRoutesRequest> result = adminService.findAllBus();

        assertEquals(1, result.size());

        BusPopularRoutesRequest result1 = result.get(0);
        assertEquals(1L, result1.getBusNum());

    }



    @Test
    @DisplayName("예약방 찾기")
    public void findAllReservation_ReturnsReservationStatusRequestList() {

        Member member = new Member(1L,"나","123456789","",
                9999L,com.metanet.metabus.member.entity.Role.ADMIN,"123456789", Grade.ALPHA);

        member.setCreatedDate(LocalDate.now().atStartOfDay());
        Bus bus1 = new Bus(1L,1L, LocalDate.now());
        Seat seat1 = new Seat(1L,1L,bus1);
        Reservation reservation1 = new Reservation(1L,member,"출발지","도착지",
                "도착시간","도착시간",LocalDate.now(),1L,seat1,"도착지",
                ReservationStatus.PAID,"도착지","도착지",1L,1L);



        reservation1.setCreatedDate(LocalDate.now().atStartOfDay());


        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation1);

        when(reservationRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate")))
                .thenReturn(reservationList);

        List<ReservationStatusRequest> result = adminService.findAllReservation();

        assertEquals(1, result.size());

        ReservationStatusRequest result1 = result.get(0);
        assertEquals(1L, result1.getMemberId());
        assertEquals(ReservationStatus.PAID, result1.getReservationStatus());
        assertNull(result1.getDeletedDate());

    }

    @Test
    @DisplayName("예약방찾기방삭제됌")
    public void findAllReservation_ReturnsReservationStatusRequestList_Null() {

        Member member = new Member(1L,"나","123456789","",
                9999L,com.metanet.metabus.member.entity.Role.ADMIN,"123456789", Grade.ALPHA);

        member.setCreatedDate(LocalDate.now().atStartOfDay());
        Bus bus1 = new Bus(1L,1L, LocalDate.now());
        Seat seat1 = new Seat(1L,1L,bus1);
        Reservation reservation1 = new Reservation(1L,member,"출발지","도착지",
                "도착시간","도착시간",LocalDate.now(),1L,seat1,"도착지",
                ReservationStatus.PAID,"도착지","도착지",1L,1L);



        reservation1.setCreatedDate(LocalDate.now().atStartOfDay());
        reservation1.setDeletedDate(LocalDate.now().atStartOfDay());

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation1);

        when(reservationRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate")))
                .thenReturn(reservationList);

        List<ReservationStatusRequest> result = adminService.findAllReservation();

        assertEquals(1, result.size());

        ReservationStatusRequest result1 = result.get(0);
        assertEquals(1L, result1.getMemberId());
        assertEquals(ReservationStatus.PAID, result1.getReservationStatus());

    }



    @Test
    @DisplayName("맴버찾기")
    public void findAllMember_ReturnsMemberInfoRequestList() {

        Member member1 = new Member(1L,"나","123456789","",
                9999L,com.metanet.metabus.member.entity.Role.ADMIN,"123456789",Grade.ALPHA);

        Member member2 = new Member(2L,"나","123456789","",
                9999L,com.metanet.metabus.member.entity.Role.ADMIN,"123456789",Grade.ALPHA);

        member1.setCreatedDate(LocalDate.now().atStartOfDay());
        member2.setCreatedDate(LocalDate.now().atStartOfDay());

        List<Member> memberList = new ArrayList<>();
        memberList.add(member1);
        memberList.add(member2);

        when(memberRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate")))
                .thenReturn(memberList);


        List<MemberInfoRequest> result = adminService.findAllMember();

        assertEquals(2, result.size());

        MemberInfoRequest result1 = result.get(0);
        assertEquals(1L, result1.getMemberId());
        assertEquals(LocalDate.now(), result1.getCreatedDate());

        MemberInfoRequest result2 = result.get(1);
        assertEquals(2L, result2.getMemberId());
        assertEquals(LocalDate.now(), result2.getCreatedDate());


    }

    @Test
    @DisplayName("맴버찾기삭제됌")
    public void findAllMember_ReturnsMemberInfoRequestList_null() {

        Member member1 = new Member(1L,"나","123456789","",
                9999L,com.metanet.metabus.member.entity.Role.ADMIN,"123456789",Grade.ALPHA);

        Member member2 = new Member(2L,"나","123456789","",
                9999L,com.metanet.metabus.member.entity.Role.ADMIN,"123456789",Grade.ALPHA);

        member1.setCreatedDate(LocalDate.now().atStartOfDay());
        member2.setCreatedDate(LocalDate.now().atStartOfDay());
        member1.setDeletedDate(LocalDate.now().atStartOfDay());
        member2.setDeletedDate(LocalDate.now().atStartOfDay());
        List<Member> memberList = new ArrayList<>();
        memberList.add(member1);
        memberList.add(member2);

        when(memberRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate")))
                .thenReturn(memberList);


        List<MemberInfoRequest> result = adminService.findAllMember();

        assertEquals(2, result.size());

        MemberInfoRequest result1 = result.get(0);
        assertEquals(1L, result1.getMemberId());
        assertEquals(LocalDate.now(), result1.getCreatedDate());

        MemberInfoRequest result2 = result.get(1);
        assertEquals(2L, result2.getMemberId());
        assertEquals(LocalDate.now(), result2.getCreatedDate());


    }

    @Test
    @DisplayName("예약 갯수 찾기")
    public void countReservation_ReturnsCountReservationList() {
                Member member = new Member(1L,"나","123456789","",
                9999L,com.metanet.metabus.member.entity.Role.ADMIN,"123456789", Grade.ALPHA);

        Bus bus1 = new Bus(1L,1L, LocalDate.now());
        Seat seat1 = new Seat(1L,1L,bus1);
        Reservation reservation1 = new Reservation(1L,member,"출발지","도착지",
                "도착시간","도착시간",LocalDate.now(),1L,seat1,"도착지",
                ReservationStatus.PAID,"도착지","도착지",1L,1L);


        Bus bus2 = new Bus(1L,1L, LocalDate.now());
        Seat seat2 = new Seat(1L,1L,bus2);
        Reservation reservation2 = new Reservation(2L,member,"출발지","도착지",
                "도착시간","도착시간",LocalDate.now(),1L,seat2,"도착지",
                ReservationStatus.PAID,"도착지","도착지",1L,1L);

        Bus bus3 = new Bus(1L,1L, LocalDate.now());
        Seat seat3 = new Seat(1L,1L,bus3);
        Reservation reservation3 = new Reservation(3L,member,"출발지","도착지",
                "도착시간","도착시간",LocalDate.now(),1L,seat3,"도착지",
                ReservationStatus.PAID,"도착지","도착지",1L,1L);

        reservation1.setCreatedDate(LocalDate.now().atStartOfDay());
        reservation2.setCreatedDate(LocalDate.now().atStartOfDay());
        reservation3.setCreatedDate(LocalDate.now().atStartOfDay());


        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);
        reservationList.add(reservation3);

        when(reservationRepository.findByReservationStatus(ReservationStatus.PAID))
                .thenReturn(reservationList);


        List<Long> result = adminService.countReservation(ReservationStatus.PAID);

        assertEquals(6, result.size());
    }
}





