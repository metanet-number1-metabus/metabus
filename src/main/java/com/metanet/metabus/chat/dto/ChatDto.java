package com.metanet.metabus.chat.dto;

import lombok.*;
import com.metanet.metabus.chat.entity.Chat;
import com.metanet.metabus.chat.entity.Room;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChatDto {
    private Long id;
    private Room room;
    private String sender;
    private String message;
    private LocalDateTime sendDate;

    public static ChatDto of(Chat chat) {
        return ChatDto.builder()
                .id(chat.getId())
                .room(chat.getRoom())
                .sender(chat.getSender())
                .message(chat.getMessage())
                .sendDate(chat.getSendDate())
                .build();
    }

    /**
     * 채팅 생성
     *
     * @param room    채팅 방
     * @param sender  보낸이
     * @param message 내용
     * @return Chat Entity
     */
    public static Chat createChat(Room room, String sender, String message) {
        return Chat.builder()
                .room(room)
                .sender(sender)
                .message(message)
                .build();
    }

}
