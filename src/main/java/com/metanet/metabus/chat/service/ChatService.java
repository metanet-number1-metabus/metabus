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
    private final RoomRepository roomRepository;
    private final ChatRepository chatRepository;



    public List<RoomDto> findAllRoom() {
        List<Room> rooms = roomRepository.findAllByDeletedDateIsNull();
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




//    public RoomDto findRoomById(Long id) {
//        Optional<Room> RoomOptional = roomRepository.findById(id);
//        return RoomOptional.map(RoomDto::of).orElse(null);
//    }


    public void createRoom(String name,Long id) {

        roomRepository.save(Room.createRoom(name,id,"미완료"));
    }

    /////////////////


    public Chat createChat(Long roomId, String sender, String message) {
        Room room = roomRepository.findById(roomId).orElseThrow();//방 찾기 -> 없는 방일 경우 여기서 예외처리
        return chatRepository.save(ChatDto.createChat(room, sender, message));
    }





    public List<ChatDto> findAllChatByRoomId(Long roomId) {
        List<Chat> chats = chatRepository.findAllByRoomId(roomId);
        return chats.stream()
                .map(ChatDto::of)
                .collect(Collectors.toList());
    }

    public void updateRoom(Long id, ChatMessage message){
        Room room = roomRepository.findById(id).orElseThrow();
        if(message.getSender().equals("운영자")){
            roomRepository.save(Room.updateRoom2(room.getId(),room.getName(),room.getMemId(),room.getCompleteYN()));
        }else{
            roomRepository.save(Room.updateRoom(room.getId(),room.getName(),room.getMemId(),room.getCompleteYN()));
        }
    }

    public void completeRoom(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        roomRepository.save(Room.completeRoom(room.getId(),room.getName(),room.getMemId(),"완료"));
    }


    public String findTitle(Long roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        String title = room.getName();
        title = title.replace("!","");
            return title;
    }


}
