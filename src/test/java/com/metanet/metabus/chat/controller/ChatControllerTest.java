package com.metanet.metabus.chat.controller;

import com.metanet.metabus.chat.dto.ChatMessage;
import com.metanet.metabus.chat.entity.Chat;
import com.metanet.metabus.chat.entity.Room;
import com.metanet.metabus.chat.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    public ChatControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("채팅메세지 테스트")
    void testChatMessage() {
        Long roomId = 1L;
        ChatMessage message = new ChatMessage();
        message.setSender("me");
        message.setMessage("Hello");
        Room room = new Room(1L,"나",1L,"완료");
        Chat chat = new Chat(room,"me","Hello");

        when(chatService.createChat(any(Long.class), any(String.class), any(String.class))).thenReturn(chat);


        ChatMessage result = chatController.test(roomId, message);

        assertEquals(roomId, result.getRoomId());
        assertEquals(message.getSender(), result.getSender());
        assertEquals(message.getMessage(), result.getMessage());
    }
}