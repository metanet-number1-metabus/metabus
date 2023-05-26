package com.metanet.metabus.member.service;

import com.metanet.metabus.common.exception.conflict.DuplicateEmailException;
import com.metanet.metabus.common.exception.not_found.MemberNotFoundException;
import com.metanet.metabus.member.dto.MemberRegisterRequest;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmailAndDeletedDateIsNull(email).orElseThrow(MemberNotFoundException::new);
    }

    @Transactional
    public Long register(MemberRegisterRequest memberRegisterRequest) {
        memberRepository.findByEmail(memberRegisterRequest.getEmail()).ifPresent(member -> {
            throw new DuplicateEmailException();
        });

        String encoded = passwordEncoder.encode(memberRegisterRequest.getPassword());

        Member member = memberRegisterRequest.toEntity(encoded);

        return memberRepository.save(member).getId();
    }
}
