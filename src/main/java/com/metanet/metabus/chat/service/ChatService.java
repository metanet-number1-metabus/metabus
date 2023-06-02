package com.metanet.metabus.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.metabus.chat.dto.ChatDto;
import com.metanet.metabus.chat.dto.ChatMessage;
import com.metanet.metabus.chat.dto.RoomDto;
import com.metanet.metabus.chat.entity.Chat;
import com.metanet.metabus.chat.entity.Room;
import com.metanet.metabus.chat.repository.ChatRepository;
import com.metanet.metabus.chat.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ObjectMapper objectMapper;
    private final RoomRepository roomRepository;
    private final ChatRepository chatRepository;

    /**
     * 모든 채팅방 찾기
     */


    public List<RoomDto> findAllRoom() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
                .map(RoomDto::of)
                .collect(Collectors.toList());
    }

    public List<RoomDto> findUserRoom(Long id) {
        List<Room> rooms = roomRepository.findUser(id);
        return rooms.stream()
                .map(RoomDto::of)
                .collect(Collectors.toList());
    }


    /**
     * 특정 채팅방 찾기
     *
     * @param id room_id
     */


    public RoomDto findRoomById(Long id) {
        Optional<Room> RoomOptional = roomRepository.findById(id);
        return RoomOptional.map(RoomDto::of).orElse(null);
    }

    /**
     * 채팅방 만들기
     *
     * @param name 방 이름
     */
    public void createRoom(String name,Long id) {

        roomRepository.save(Room.createRoom(name,id));
    }

    /////////////////


    /**
     * 채팅 생성
     *
     * @param roomId  채팅방 id
     * @param sender  보낸이
     * @param message 내용
     */
    public Chat createChat(Long roomId, String sender, String message) {
        Room room = roomRepository.findById(roomId).orElseThrow();//방 찾기 -> 없는 방일 경우 여기서 예외처리
        return chatRepository.save(ChatDto.createChat(room, sender, message));
    }


    /**
     * 채팅방 채팅내용 불러오기
     *
     * @param roomId 채팅방 id
     */


    public List<ChatDto> findAllChatByRoomId(Long roomId) {
        List<Chat> chats = chatRepository.findAllByRoomId(roomId);
        return chats.stream()
                .map(ChatDto::of)
                .collect(Collectors.toList());
    }

    public void updateRoom(Long id, ChatMessage message){
        Room room = roomRepository.findById(id).orElseThrow();
        if(message.getSender().equals("운영자")){
            roomRepository.save(Room.updateRoom2(room.getId(),room.getName(),room.getMemId()));
        }else{
            roomRepository.save(Room.updateRoom(room.getId(),room.getName(),room.getMemId()));
        }
    }
}
