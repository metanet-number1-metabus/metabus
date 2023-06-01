package com.metanet.metabus.chat.controller;

import com.metanet.metabus.chat.dto.ChatDto;
import com.metanet.metabus.chat.dto.RoomDto;
import com.metanet.metabus.chat.service.ChatService;
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
    public String joinRoom(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) MemberDto memberDto,@PathVariable(required = false) Long roomId, Model model) {
        // 채팅탭클릭시 채팅리스트만 띄우게하기위해 -1로 보냄
        if (roomId != -1) {
            List<ChatDto> chatList = chatService.findAllChatByRoomId(roomId);
            model.addAttribute("roomId", roomId);
            model.addAttribute("chatList", chatList);
        }
        List<RoomDto> roomList = null;
        if(memberDto.getRole().name().equals("ADMIN")){
            roomList = chatService.findAllRoom();
        } else if (memberDto.getRole().name().equals("USER")){
            roomList = chatService.findUserRoom(memberDto.getId());
        }
        model.addAttribute("roomList", roomList);
        model.addAttribute("roomForm", new RoomForm());
        return "chat/room";
    }


    /**
     * 채팅방 등록
     *
     * @param form
     */
    @PostMapping("/chat/room")
    @ResponseBody
    public Long createRoom(HttpSession session, String name) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        Long id = loginMember.getId();
        if(name!=null) {
            // 세션에 값을 저장하거나 가져와 사용합니다.
            chatService.createRoom(name, id);
            return id;
        }else{
            return (long) -1;
        }

    }


    /**
     * 방만들기 폼
     */
    @GetMapping("/roomForm")
    public String roomForm() {
        return "chat/roomForm";
    }

}
