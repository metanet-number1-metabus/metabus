package com.metanet.metabus.security.entrypoint;

import com.metanet.metabus.common.exception.GlobalExceptionHandler;
import com.metanet.metabus.member.entity.Member;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Log4j2
public class CustomAccessDeniedEntryPoint implements AccessDeniedHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());

        if (accessDeniedException instanceof AccessDeniedHandler) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && ((Member) authentication.getPrincipal()).getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
                request.setAttribute("msg", "접근권한이 없는 사용자 입니다.");
                request.setAttribute("nextPage", "/");
            } else {
//                request.setAttribute("msg", "로그인 권한이 없는 아이디 입니다.");
//                request.setAttribute("nextPage", "/login");
//                response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                SecurityContextHolder.clearContext(); //
            }
        } else {
            logger.info(accessDeniedException.getClass().getCanonicalName());
        }
        // 인증, 인가 거부 됐을 때 이동될 페이지
        request.getRequestDispatcher("/error/401.html");

    }
}
