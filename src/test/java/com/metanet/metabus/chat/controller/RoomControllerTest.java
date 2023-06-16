package com.metanet.metabus.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.metabus.chat.dto.ChatDto;
import com.metanet.metabus.chat.dto.RoomDto;
import com.metanet.metabus.chat.entity.Room;
import com.metanet.metabus.chat.service.ChatService;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Grade;
import com.metanet.metabus.member.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    @DisplayName("방 완료처리")
    void completeRoomRedirectToDefaultPage() throws Exception {

        Long roomId = 1L;
        doNothing().when(chatService).completeRoom(roomId);

        mockMvc.perform(get("/complete/{roomId}", roomId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/-1"));
    }

    @Test
    @DisplayName("방보이기 유저")
    void joinRoomWithValidRoomId() throws Exception {

        MockHttpSession session = new MockHttpSession();

        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);

        Long roomId = 1L;


        mockMvc.perform(get("/{roomId}", roomId)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("roomId"))
                .andExpect(model().attributeExists("chatList"))
                .andExpect(model().attributeExists("roomList"))
                .andExpect(view().name("chat/room"));
    }

    @Test
    @DisplayName("방보이기 운영자")
    void joinRoomWithValidRoomIdA() throws Exception {

        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                Role.ADMIN, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);


        Long roomId = 1L;

        mockMvc.perform(get("/{roomId}", roomId)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("roomId"))
                .andExpect(model().attributeExists("chatList"))
                .andExpect(model().attributeExists("roomList"))
                .andExpect(view().name("chat/room"));
    }

    @Test
    @DisplayName("방보기 세션없음")
    void joinRoomWithNull() throws Exception {

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/{roomId}", 1L).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/login"));
    }

    @Test
    @DisplayName("방초기화면 ")
    void joinRoomWithNegativeRoomId() throws Exception {

        MockHttpSession session = new MockHttpSession();
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);
        session.setAttribute("loginMember", memberDto);

        mockMvc.perform(get("/-1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("chat/room"))
                .andExpect(model().attributeExists("roomList"));
    }

    @Test
    @DisplayName("유저 방리스트")
    void joinRoomAsUser() throws Exception {

        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);

        Room room = new Room(1L, "나", 1L, "완료");

        List<ChatDto> fakeChatList = Arrays.asList(
                new ChatDto(1L, room, "User1", "Hello", LocalDateTime.now()),
                new ChatDto(2L, room, "User2", "Hi", LocalDateTime.now())
        );


        List<RoomDto> fakeRoomList = Arrays.asList(
                new RoomDto(1L, "Room 1", 1L, "완료"),
                new RoomDto(2L, "Room 2", 1L, "완료")
        );

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginMember", memberDto);

        when(chatService.findAllChatByRoomId(1L)).thenReturn(fakeChatList);


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
    @DisplayName("방만들기 이름있음")
    void createRoom_validName_success() throws Exception {

        MockHttpSession session = new MockHttpSession();
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(post("/chat/room")
                        .session(session)
                        .param("name", "name")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("방만들기 이름공백")
    void createRoom_validName_fail() throws Exception {

        MockHttpSession session = new MockHttpSession();
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(post("/chat/room")
                        .session(session)
                        .param("name", "")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("-1"));
    }

    @Test
    @DisplayName("방만들기 이름null")
    void createRoom_validName_fail_null() throws Exception {

        MockHttpSession session = new MockHttpSession();
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(post("/chat/room")
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("-1"));
    }


}