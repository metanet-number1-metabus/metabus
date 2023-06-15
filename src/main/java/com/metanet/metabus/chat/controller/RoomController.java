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


    @GetMapping("/{roomId}")
    public String joinRoom(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) MemberDto memberDto,@PathVariable(required = false) Long roomId, Model model) {
        if(memberDto==null){
            invalidSession invalidSession = new invalidSession();
            return "redirect:/member/login";
        }


        if (roomId != -1) {
            List<ChatDto> chatList = chatService.findAllChatByRoomId(roomId);
            model.addAttribute("roomId", roomId);
            model.addAttribute("chatList", chatList);
        }
        List<RoomDto> roomList = null;
        if(memberDto.getRole().name().equals("ADMIN")){
            roomList = chatService.findAllRoom();
        }else{
            roomList = chatService.findUserRoom(memberDto.getId());
        }
        model.addAttribute("roomList", roomList);

        return "chat/room";
    }

    @GetMapping("/complete/{roomId}")
    public String complete(@PathVariable(required = false) Long roomId){
        chatService.completeRoom(roomId);
        return "redirect:/-1";
    }

    @PostMapping("/chat/room")
    @ResponseBody
    public Long createRoom(HttpSession session, String name) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        Long id = loginMember.getId();
        if(name!=null&&!name.equals("")) {
            // 세션에 값을 저장하거나 가져와 사용합니다.
            chatService.createRoom(name, id);
            return id;
        }else{
            return (long) -1;
        }

    }



}
