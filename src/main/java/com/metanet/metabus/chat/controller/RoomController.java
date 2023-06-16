package com.metanet.metabus.chat.controller;

import com.metanet.metabus.chat.dto.ChatDto;
import com.metanet.metabus.chat.dto.RoomDto;
import com.metanet.metabus.chat.service.ChatService;
import com.metanet.metabus.common.exception.unauthorized.invalidSession;
import com.metanet.metabus.member.controller.SessionConst;
import com.metanet.metabus.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RoomController {

    private final ChatService chatService;

    /**
     * 채팅방 참여하기
     *
     * @param roomId 채팅방 id
     */

    @GetMapping("/{roomId}")
    public String joinRoom(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) MemberDto memberDto, @PathVariable(required = false) Long roomId, HttpSession session, Model model) {
        if (memberDto == null) {
            invalidSession invalidSession = new invalidSession();
            return "redirect:/member/login";
        }


        if (roomId != -1) {
            List<ChatDto> chatList = chatService.findAllChatByRoomId(roomId);
            String title = chatService.findTitle(roomId);
            model.addAttribute("roomId", roomId);
            model.addAttribute("chatList", chatList);
            model.addAttribute("title",title);
        }
        List<RoomDto> roomList = null;
        if (memberDto.getRole().name().equals("ADMIN")) {
            roomList = chatService.findAllRoom();
        } else {
            roomList = chatService.findUserRoom(memberDto.getId());
        }
        model.addAttribute("roomList", roomList);

        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        model.addAttribute("memberDto", loginMember);

        return "chat/room";
    }

    @GetMapping("/complete/{roomId}")
    public String complete(@PathVariable(required = false) Long roomId, HttpSession session, Model model) {
        chatService.completeRoom(roomId);

        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        model.addAttribute("memberDto", loginMember);

        return "redirect:/-1";
    }

    // 채팅방 등록
    @PostMapping("/chat/room")
    @ResponseBody
    public Long createRoom(HttpSession session, String name) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        Long id = loginMember.getId();
        if (name != null && !name.equals("")) {
            // 세션에 값을 저장하거나 가져와 사용합니다.
            name = name.replace(" ","");
            chatService.createRoom(name, id);
            return id;
        } else {
            return (long) -1;
        }
    }

}
