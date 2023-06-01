package com.metanet.metabus.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.cors();

//        http.authorizeRequests()
//                .antMatchers("/member/register", "/member/login").permitAll()
////                .antMatchers("/member/mypage").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
//                .antMatchers(HttpMethod.GET, "/**").permitAll()
//                .antMatchers(HttpMethod.POST, "/**").authenticated()
//                .antMatchers(HttpMethod.PUT, "/**").authenticated()
//                .antMatchers(HttpMethod.DELETE, "/**").authenticated();

        return http.build();
    }
}
