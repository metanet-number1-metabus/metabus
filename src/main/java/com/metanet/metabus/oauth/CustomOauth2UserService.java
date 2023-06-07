package com.metanet.metabus.oauth;

import com.metanet.metabus.member.controller.SessionConst;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.entity.Role;
import com.metanet.metabus.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Member member = memberRepository.findByEmail(attributes.getEmail())
                .orElseGet(() -> {
                    Member savedMember = Member.builder()
                            .id((Long) oAuth2User.getAttributes().get("id"))
                            .email(attributes.getEmail())
                            .password(" ")
                            .name(attributes.getName())
                            .mileage(0L)
                            .role(Role.GUEST)
                            .phoneNum("010-0000-0000")
                            .build();

                    memberRepository.save(savedMember);

                    return savedMember;
                });

        MemberDto memberDto = MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole())
                .mileage(member.getMileage())
                .phoneNum(member.getPhoneNum())
                .build();

        httpSession.setAttribute(SessionConst.LOGIN_MEMBER, memberDto);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("GUEST")),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }
}