package com.metanet.metabus.mileage.service;

import com.metanet.metabus.bus.entity.Bus;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.ReservationStatus;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Grade;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.repository.MemberRepository;
import com.metanet.metabus.mileage.dto.MileageDto;
import com.metanet.metabus.mileage.entity.Mileage;
import com.metanet.metabus.mileage.entity.SaveStatus;
import com.metanet.metabus.mileage.repository.MileageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class MileageServiceTest {

    private final MemberRepository memberRepository = Mockito.mock(MemberRepository.class);
    private final MileageRepository mileageRepository = Mockito.mock(MileageRepository.class);
    private final MileageService mileageService = new MileageService(memberRepository, mileageRepository);

    private final LocalDate departureDate = LocalDate.now().plusDays(30);
    private final Long busNum = 10000L;
    private final LocalDateTime createdDate = LocalDateTime.now();


    private final MemberDto memberDto = MemberDto.builder()
            .id(1L)
            .name("name")
            .build();

    Member member = Member.builder()
            .id(1L)
            .name("name")
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


    @Test
    @DisplayName("마일리지 적립 성공: ALPHA 등급")
    void save_mileage_success1() {

        Member member = Member.builder()
                .id(1L)
                .grade(Grade.ALPHA)
                .build();

        Reservation reservation = Reservation.builder()
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

        Mileage mileage = Mileage.builder()
                .member(member)
                .point(633L)
                .saveStatus(SaveStatus.UP)
                .build();

        Mileage expected = mileageService.saveMileage(reservation);

        assertEquals(expected.getMember(), mileage.getMember());
        assertEquals(expected.getPoint(), mileage.getPoint());
        assertEquals(expected.getSaveStatus(), mileage.getSaveStatus());
    }

    @Test
    @DisplayName("마일리지 적립 성공: META 등급")
    void save_mileage_success2() {

        Member member = Member.builder()
                .id(1L)
                .grade(Grade.META)
                .build();

        Reservation reservation = Reservation.builder()
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

        Mileage mileage = Mileage.builder()
                .member(member)
                .point(1055L)
                .saveStatus(SaveStatus.UP)
                .build();

        Mileage expected = mileageService.saveMileage(reservation);

        assertEquals(expected.getMember(), mileage.getMember());
        assertEquals(expected.getPoint(), mileage.getPoint());
        assertEquals(expected.getSaveStatus(), mileage.getSaveStatus());
    }

    @Test
    @DisplayName("누적 마일리지 조회 성공")
    void find_all_mileage_success() {

        List<Mileage> mileages = new ArrayList<>();

        Mileage mileage = Mileage.builder()
                .member(member)
                .point(1000L)
                .saveStatus(SaveStatus.UP)
                .build();

        mileages.add(mileage);

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(mileageRepository.findByMemberAndSaveStatus(member, mileage.getSaveStatus())).thenReturn(mileages);

        Long expected = mileageService.findAllMileage(memberDto);

        assertEquals(expected, mileage.getPoint());
    }

    @Test
    @DisplayName("누적 사용 마일리지 조회 성공")
    void find_used_mileage_success() {

        List<Mileage> mileages = new ArrayList<>();

        Mileage mileage = Mileage.builder()
                .member(member)
                .point(1000L)
                .saveStatus(SaveStatus.DOWN)
                .build();

        mileages.add(mileage);

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(mileageRepository.findByMemberAndSaveStatus(member, mileage.getSaveStatus())).thenReturn(mileages);

        Long expected = mileageService.findUsedMileage(memberDto);

        assertEquals(expected, mileage.getPoint());
    }

    @Test
    @DisplayName("누적 사용 마일리지 조회 성공")
    void find_mileage_success() {

        List<Mileage> savedMileages = new ArrayList<>();
        List<Mileage> usedMileages = new ArrayList<>();

        Mileage savedMileage = Mileage.builder()
                .member(member)
                .point(1000L)
                .saveStatus(SaveStatus.UP)
                .build();

        Mileage usedMileage = Mileage.builder()
                .member(member)
                .point(1000L)
                .saveStatus(SaveStatus.DOWN)
                .build();

        savedMileages.add(savedMileage);
        usedMileages.add(usedMileage);

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(mileageRepository.findByMemberAndSaveStatus(member, savedMileage.getSaveStatus())).thenReturn(savedMileages);
        when(mileageRepository.findByMemberAndSaveStatus(member, usedMileage.getSaveStatus())).thenReturn(usedMileages);

        Long expected = mileageService.findMileage(memberDto);

        assertEquals(expected, 0);
    }

    @Test
    @DisplayName("마일리지 내역 조회 성공")
    void find_mileage_list_success() {
        List<Mileage> mileageList = new ArrayList<>();

        Mileage savedMileage = Mileage.builder()
                .member(member)
                .point(1000L)
                .saveStatus(SaveStatus.UP)
                .build();
        savedMileage.setCreatedDate(createdDate);

        Mileage usedMileage = Mileage.builder()
                .member(member)
                .point(1000L)
                .saveStatus(SaveStatus.DOWN)
                .build();
        usedMileage.setCreatedDate(createdDate);

        mileageList.add(savedMileage);
        mileageList.add(usedMileage);

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));
        when(mileageRepository.findByMember(member)).thenReturn(mileageList);

        List<MileageDto> result = mileageService.findMileageByMember(memberDto);

        assertEquals(2, result.size());

        MileageDto savedMileageDto = result.get(0);
        assertEquals("name", savedMileageDto.getName());
        assertEquals(1000L, savedMileageDto.getPoint());
        assertEquals("적립", savedMileageDto.getSaveStatus());
        assertEquals(createdDate.toLocalDate(), savedMileageDto.getCreatedDate());

        MileageDto usedMileageDto = result.get(1);
        assertEquals("name", usedMileageDto.getName());
        assertEquals(1000L, usedMileageDto.getPoint());
        assertEquals("사용", usedMileageDto.getSaveStatus());
        assertEquals(createdDate.toLocalDate(), usedMileageDto.getCreatedDate());

    }

    @Test
    @DisplayName("멤버 구하기 성공")
    void get_member_success() {

        when(memberRepository.findById(memberDto.getId())).thenReturn(Optional.of(member));

        Member result = mileageService.getMember(memberDto);

        assertEquals(member, result);

    }

}
