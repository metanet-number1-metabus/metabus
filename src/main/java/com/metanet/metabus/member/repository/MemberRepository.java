package com.metanet.metabus.member.repository;

import com.metanet.metabus.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndDeletedDateIsNull(String email);
    Optional<Member> findByEmail(String email);
}
