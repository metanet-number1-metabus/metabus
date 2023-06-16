package com.metanet.metabus.mileage.service;

import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.common.exception.not_found.MemberNotFoundException;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Grade;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.repository.MemberRepository;
import com.metanet.metabus.mileage.dto.MileageDto;
import com.metanet.metabus.mileage.entity.Mileage;
import com.metanet.metabus.mileage.entity.SaveStatus;
import com.metanet.metabus.mileage.repository.MileageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MileageService {

    private final MemberRepository memberRepository;
    private final MileageRepository mileageRepository;

    // 등급에 따른 마일리지 적립
    public Mileage saveMileage(Reservation reservation) {

        Member member = reservation.getMember();
        Grade grade = member.getGrade();

        Long payment = reservation.getPayment();
        long point = 0L;

        if (grade == Grade.ALPHA) {
            point = payment / 100 * 3;
        } else if (grade == Grade.META) {
            point = payment / 100 * 5;
        }

        return Mileage.builder()
                .member(member)
                .point(point)
                .saveStatus(SaveStatus.UP)
                .build();

    }

    // 누적 마일리지 조회
    public Long findAllMileage(MemberDto memberDto) {

        long allMileage = 0L;

        Member member = getMember(memberDto);

        List<Mileage> allMileageList = mileageRepository.findByMemberAndSaveStatus(member, SaveStatus.UP);
        for (Mileage mileage : allMileageList) {
            allMileage += mileage.getPoint();
        }

        return allMileage;
    }

    // 누적 사용 마일리지 조회
    public Long findUsedMileage(MemberDto memberDto) {

        long usedMileage = 0L;

        Member member = getMember(memberDto);

        List<Mileage> usedMileageList = mileageRepository.findByMemberAndSaveStatus(member, SaveStatus.DOWN);
        for (Mileage mileage : usedMileageList) {
            usedMileage += mileage.getPoint();
        }

        return usedMileage;
    }

    // 현재 마일리지 조회 및 멤버에 저장
    public Long findMileage(MemberDto memberDto) {
        Long allMileage = findAllMileage(memberDto);
        Long usedMileage = findUsedMileage(memberDto);

        Long mileage = allMileage - usedMileage;
        Member member = getMember(memberDto);
        member.updateMileage(mileage);
        memberRepository.save(member);

        return mileage;
    }

    public List<MileageDto> findMileageByMember(MemberDto memberDto) {

        List<MileageDto> mileageDtoList = new ArrayList<>();

        Member member = getMember(memberDto);
        String memberName = member.getName();

        String mileageSaveStatus = null;
        List<Mileage> mileageList = mileageRepository.findByMember(member);

        for (Mileage mileage : mileageList) {
            Long point = mileage.getPoint();

            SaveStatus saveStatus = mileage.getSaveStatus();
            if (saveStatus == SaveStatus.UP) {
                mileageSaveStatus = "적립";
            } else if (saveStatus == SaveStatus.DOWN) {
                mileageSaveStatus = "사용";
            }

            LocalDate createdDate = mileage.getCreatedDate().toLocalDate();

            mileageDtoList.add(MileageDto.builder()
                    .name(memberName)
                    .point(point)
                    .saveStatus(mileageSaveStatus)
                    .createdDate(createdDate)
                    .build());

        }

        return mileageDtoList;
    }

    public Member getMember(MemberDto memberDto) {
        Long memberId = memberDto.getId();
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }
}
