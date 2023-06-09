package com.metanet.metabus.config;

import com.metanet.metabus.oauth.CustomOauth2UserService;
import com.metanet.metabus.oauth.OAuth2LoginFailureHandler;
import com.metanet.metabus.oauth.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOauth2UserService customOauth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

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

        http.oauth2Login()
                .successHandler(oAuth2LoginSuccessHandler)
                .failureHandler(oAuth2LoginFailureHandler)
                .userInfoEndpoint()
                .userService(customOauth2UserService);

        return http.build();
    }
}
