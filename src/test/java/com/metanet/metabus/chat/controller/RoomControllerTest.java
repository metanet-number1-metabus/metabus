package com.metanet.metabus.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.metabus.chat.dto.ChatDto;
import com.metanet.metabus.chat.dto.RoomDto;
import com.metanet.metabus.chat.entity.Room;
import com.metanet.metabus.chat.service.ChatService;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
@WithMockUser
class RoomControllerTest {
    @MockBean
    ChatService chatService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @Test
    @DisplayName("Complete Room")
    void completeRoomRedirectToDefaultPage() throws Exception {

        Long roomId = 1L;
        doNothing().when(chatService).completeRoom(roomId);

        mockMvc.perform(get("/complete/{roomId}", roomId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/-1"));
    }

    @Test
    @DisplayName("Join Room ID")
    void joinRoomWithValidRoomId() throws Exception {
        // Create a mock session
        MockHttpSession session = new MockHttpSession();

        // Create a memberDto representing a logged-in user
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789");

        // Set the memberDto in the session
        session.setAttribute("loginMember", memberDto);

        // Set up a valid room ID
        Long roomId = 1L;

        // Perform the request to join the room
        mockMvc.perform(get("/{roomId}", roomId)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("roomId"))
                .andExpect(model().attributeExists("chatList"))
                .andExpect(model().attributeExists("roomList"))
                .andExpect(view().name("chat/room"));
    }

    @Test
    @DisplayName("Join Room ADMIN")
    void joinRoomWithValidRoomIdA() throws Exception {
        // Create a mock session
        MockHttpSession session = new MockHttpSession();

        // Create a memberDto representing a logged-in user
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                Role.ADMIN, "123456789");

        // Set the memberDto in the session
        session.setAttribute("loginMember", memberDto);

        // Set up a valid room ID
        Long roomId = 1L;

        // Perform the request to join the room
        mockMvc.perform(get("/{roomId}", roomId)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("roomId"))
                .andExpect(model().attributeExists("chatList"))
                .andExpect(model().attributeExists("roomList"))
                .andExpect(view().name("chat/room"));
    }

    @Test
    @DisplayName("Join Room null")
    void joinRoomWithNull() throws Exception {

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/{roomId}",1L).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/login"));
    }

    @Test
    @DisplayName("Join Room -1")
    void joinRoomWithNegativeRoomId() throws Exception {

        MockHttpSession session = new MockHttpSession();
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789");
        session.setAttribute("loginMember", memberDto);

        mockMvc.perform(get("/-1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("chat/room"))
                .andExpect(model().attributeExists("roomList"));
    }

    @Test
    @DisplayName("Join Room as User")
    void joinRoomAsUser() throws Exception {

        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789");

        Room room = new Room(1L,"나",1L,"완료");
        // 가짜 채팅 리스트
        List<ChatDto> fakeChatList = Arrays.asList(
                new ChatDto(1L, room, "User1", "Hello", LocalDateTime.now()),
                new ChatDto(2L, room, "User2", "Hi", LocalDateTime.now())
        );

        // 가짜 방 리스트
        List<RoomDto> fakeRoomList = Arrays.asList(
                new RoomDto(1L, "Room 1", 1L, "완료"),
                new RoomDto(2L, "Room 2", 1L, "완료")
        );

        // memberDto를 세션에 설정
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginMember", memberDto);

        // chatService의 findAllChatByRoomId() 메서드에 대한 Mock 설정
        when(chatService.findAllChatByRoomId(1L)).thenReturn(fakeChatList);

        // chatService의 findUserRoom() 메서드에 대한 Mock 설정
        when(chatService.findUserRoom(memberDto.getId())).thenReturn(fakeRoomList);

        mockMvc.perform(get("/{roomId}", 1L)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("chat/room"))
                .andExpect(model().attribute("roomId", 1L))
                .andExpect(model().attribute("chatList", fakeChatList))
                .andExpect(model().attribute("roomList", fakeRoomList));
    }

    @Test
    @DisplayName("Create Room")
    void createRoom() throws Exception {
        // 입력 데이터
        String name = "Room 1";
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                Role.USER, "123456789");

        // 세션에 loginMember 객체 설정
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginMember", memberDto);

        // chatService의 createRoom() 메서드에 대한 Mock 설정
        doNothing().when(chatService).createRoom(name, memberDto.getId());

        mockMvc.perform(post("/chat/room")
                        .session(session)
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(content().string(memberDto.getId().toString()));
    }
}