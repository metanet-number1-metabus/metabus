package com.metanet.metabus.mileage.repository;

import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.mileage.entity.Mileage;
import com.metanet.metabus.mileage.entity.SaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MileageRepository extends JpaRepository<Mileage, Long> {

    List<Mileage> findByMemberAndSaveStatus(Member member, SaveStatus saveStatus);

    List<Mileage> findByMemberOrderByCreatedDateDesc(Member member);

}
