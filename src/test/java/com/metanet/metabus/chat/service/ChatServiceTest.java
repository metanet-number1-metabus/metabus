package com.metanet.metabus.chat.service;

import com.metanet.metabus.chat.dto.ChatDto;
import com.metanet.metabus.chat.dto.ChatMessage;
import com.metanet.metabus.chat.dto.RoomDto;
import com.metanet.metabus.chat.entity.Chat;
import com.metanet.metabus.chat.entity.Room;
import com.metanet.metabus.chat.repository.ChatRepository;
import com.metanet.metabus.chat.repository.RoomRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatServiceTest {

    private final ChatRepository chatRepository = mock(ChatRepository.class);
    private final RoomRepository roomRepository = mock(RoomRepository.class);
    private final ChatService chatService = new ChatService(roomRepository, chatRepository);


    @Test
    @DisplayName("모든 방 조회")
    void findAllRoom() {

        Room room1 = new Room(1L, "Room 1", 1L, "왼료");
        Room room2 = new Room(2L, "Room 2", 1L, "왼료");
        List<Room> fakeRooms = Arrays.asList(room1, room2);

        when(roomRepository.findAllByDeletedDateIsNull()).thenReturn(fakeRooms);


        List<RoomDto> expectedRoomDtos = Arrays.asList(
                new RoomDto(1L, "Room 1", 1L, "왼료"),
                new RoomDto(2L, "Room 2", 1L, "왼료")
        );

        List<RoomDto> result = Assertions.assertDoesNotThrow(() -> chatService.findAllRoom());

        assertEquals(expectedRoomDtos.size(), result.size());
    }


    @Test
    @DisplayName("사용자 방 조회")
    void findUserRoom() {
        Long userId = 1L;

        // 가짜 Room 데이터 생성
        Room room1 = new Room(1L, "Room 1", 1L, "왼료");
        Room room2 = new Room(2L, "Room 2", 1L, "왼료");
        List<Room> fakeRooms = Arrays.asList(room1, room2);

        // RoomRepository의 findUser() 메서드에 대한 Mock 설정
        when(roomRepository.findUser(userId)).thenReturn(fakeRooms);

        // RoomDto로 변환하는 로직 테스트
        List<RoomDto> expectedRoomDtos = Arrays.asList(
                new RoomDto(1L, "Room 1", 1L, "왼료"),
                new RoomDto(2L, "Room 2", 1L, "왼료")
        );

        // 테스트 대상 메서드 호출
        List<RoomDto> result = Assertions.assertDoesNotThrow(() -> chatService.findUserRoom(userId));

        // 예상 결과와 실제 결과 비교
        assertEquals(expectedRoomDtos.size(), result.size());
    }

    @Test
    @DisplayName("방 생성")
    void createRoom() {

        Room room = new Room(1L, "Room 1", 1L, "미완료");

        when(roomRepository.save(any(Room.class))).thenReturn(room);

        chatService.createRoom("Room 1", 1L);

    }

    @Test
    @DisplayName("채팅 생성")
    void createChat() {
        Long roomId = 1L;



        Room room = new Room(1L, "Room 1", 1L, "완료");


        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));


        Chat expectedChat = new Chat(room, "User1", "Hello");

        when(chatRepository.save(any(Chat.class))).thenReturn(expectedChat);


        Chat result = Assertions.assertDoesNotThrow(() -> chatService.createChat(roomId, "User1", "Hello"));

        assertEquals(expectedChat, result);

    }

    @Test
    @DisplayName("방 ID로 채팅 전체 조회")
    void findAllChatByRoomId() {
        Long roomId = 1L;

        Room room = new Room(1L, "방이름", 1L, "완료");

        Chat chat1 = new Chat(room, "User1", "Hello");
        Chat chat2 = new Chat(room, "User2", "Hi");
        List<Chat> fakeChats = Arrays.asList(chat1, chat2);

        when(chatRepository.findAllByRoomId(roomId)).thenReturn(fakeChats);


        List<ChatDto> expectedChatDtos = Arrays.asList(
                new ChatDto(null, room, "User1", "Hello", chat1.getSendDate()),
                new ChatDto(null, room, "User2", "Hi", chat2.getSendDate())
        );


        List<ChatDto> result = Assertions.assertDoesNotThrow(() -> chatService.findAllChatByRoomId(roomId));

        assertEquals(expectedChatDtos.size(), result.size());
    }

    @Test
    @DisplayName("방 완료 처리")
    void completeRoom() {
        Long roomId = 1L;

        Room room = new Room(1L, "방이름", 1L, "미완료");

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        Room updatedRoom = new Room(1L, "방이름", 1L, "완료");

        when(roomRepository.save(any(Room.class))).thenReturn(updatedRoom);

        Assertions.assertDoesNotThrow(() -> chatService.completeRoom(roomId));

        assertEquals("완료", updatedRoom.getCompleteYN());
    }

    @Test
    @DisplayName("방 업데이트 - 운영자")
    void updateRoom_Admin() {
        Long roomId = 1L;
        ChatMessage message = new ChatMessage(1L, "운영자", "새로운 메시지", LocalDateTime.now());


        Room room = new Room(1L, "방이름", 1L, "미완료");

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        Room updatedRoom = new Room(1L, "방이름", 1L, "완료");

        when(roomRepository.save(any(Room.class))).thenReturn(updatedRoom);

        Assertions.assertDoesNotThrow(() -> chatService.updateRoom(roomId, message));

        assertEquals("완료", updatedRoom.getCompleteYN());
    }

    @Test
    @DisplayName("방 업데이트 - 일반 사용자")
    void updateRoom_User() {
        Long roomId = 1L;
        ChatMessage message = new ChatMessage(1L, "운영자 아님", "새로운 메시지", LocalDateTime.now());

        Room room = new Room(1L, "방이름", 1L, "미완료");

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        Room updatedRoom = new Room(1L, "방이름", 1L, "미완료");

        when(roomRepository.save(any(Room.class))).thenReturn(updatedRoom);

        Assertions.assertDoesNotThrow(() -> chatService.updateRoom(roomId, message));

        assertEquals("미완료", updatedRoom.getCompleteYN());
    }


}