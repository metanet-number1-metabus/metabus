package com.metanet.metabus.chat.controller;

import com.metanet.metabus.chat.dto.RoomDto;
import com.metanet.metabus.chat.entity.Room;
import com.metanet.metabus.chat.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.metanet.metabus.chat.dto.ChatMessage;
import com.metanet.metabus.chat.entity.Chat;
import com.metanet.metabus.chat.service.ChatService;


@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/{roomId}") //메서드 호출
    @SendTo("/room/{roomId}")   //구독하고 있는 장소로 메시지 전송 (목적지)
    public ChatMessage test(@DestinationVariable Long roomId, ChatMessage message) {

        chatService.updateRoom(roomId,message);
        //채팅 저장
        Chat chat = chatService.createChat(roomId, message.getSender(), message.getMessage());
        return ChatMessage.builder()
                .roomId(roomId)
                .sender(chat.getSender())
                .message(chat.getMessage())
                .build();
    }

}
