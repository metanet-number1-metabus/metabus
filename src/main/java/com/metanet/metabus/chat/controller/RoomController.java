package com.metanet.metabus.chat.controller;

import com.metanet.metabus.chat.dto.ChatDto;
import com.metanet.metabus.chat.dto.RoomDto;
import com.metanet.metabus.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String joinRoom(@PathVariable(required = false) Long roomId, Model model) {
        // 채팅탭클릭시 채팅리스트만 띄우게하기위해 -1로 보냄
        if (roomId != -1) {
            List<ChatDto> chatList = chatService.findAllChatByRoomId(roomId);
            model.addAttribute("roomId", roomId);
            model.addAttribute("chatList", chatList);
        }
        List<RoomDto> roomList = chatService.findAllRoom();
        model.addAttribute("roomList", roomList);
        return "chat/room";
    }


    /**
     * 채팅방 등록
     *
     * @param form
     */
    @PostMapping("/room")
    public String createRoom(RoomForm form) {
        chatService.createRoom(form.getName());
        return "redirect:/roomList";
    }


    /**
     * 방만들기 폼
     */
    @GetMapping("/roomForm")
    public String roomForm() {
        return "chat/roomForm";
    }

}
