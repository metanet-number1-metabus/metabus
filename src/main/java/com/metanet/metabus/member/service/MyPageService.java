package com.metanet.metabus.member.service;

import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.ReservationStatus;
import com.metanet.metabus.common.exception.not_found.MemberNotFoundException;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.repository.MemberRepository;
import com.metanet.metabus.member.repository.MyPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MyPageRepository myPageRepository;
    private final MemberRepository memberRepository;

    public List<Reservation> selectTickets(MemberDto memberDto) {

        Long memberId = memberDto.getId();
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        return myPageRepository.findByMemberAndDeletedDateIsNullAndReservationStatus(member, ReservationStatus.PAID);
    }

    public Timestamp selectDates(Long id) {
        Timestamp dates = myPageRepository.myDates(id);
        return dates;
    }

    public String selectGrade(Long id) {
        String grade = myPageRepository.myGrede(id);
        return grade;
    }

    public Long selectMileage(Long id) {
        Long mileage = myPageRepository.myMileage(id);
        return mileage;
    }
}
