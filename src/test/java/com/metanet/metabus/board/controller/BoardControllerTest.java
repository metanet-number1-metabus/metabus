package com.metanet.metabus.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.metabus.board.AwsS3;
import com.metanet.metabus.board.ImageUtils;
import com.metanet.metabus.board.dto.LostBoardDto;
import com.metanet.metabus.board.service.BoardService;
import com.metanet.metabus.member.dto.MemberDto;
import com.metanet.metabus.member.entity.Grade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@WithMockUser
class BoardControllerTest {


    @MockBean
    BoardService boardService;

    @MockBean
    AwsS3 awsS3;

    @MockBean
    ImageUtils imageUtils;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("게시판 글쓰기 폼 관리자로 로그인한 경우")
    void boardWriteFormWithAdminUser() throws Exception {

        MockHttpSession session = new MockHttpSession();

        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.ADMIN, "123456789", Grade.ALPHA);

        session.setAttribute("loginMember", memberDto);

        mockMvc.perform(get("/board/write").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("board/boardwrite"));

    }

    @Test
    @DisplayName("게시판 글쓰기 폼 세션에 값이없는경우")
    void boardWriteFormRedirectToLoginWhenSessionIsNull() throws Exception {

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/board/write").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/login"));
    }

    @Test
    @DisplayName("게시판 글쓰기 폼 운영자가 아닌경우")
    void boardWriteFormRedirectToListWhenUserRoleIsNotAdmin() throws Exception {

        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);

        mockMvc.perform(get("/board/write").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
    }


    @Test
    @DisplayName("게시판 목록 조회 list 속성이 존재하는 경우 웹")
    void boardListTest_WithListAttributeW() throws Exception {

        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);

        List<LostBoardDto> boardList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        boardList.add(new LostBoardDto(1L, "제목", "내용", now));

        Pageable pageable = PageRequest.of(0, 10);
        Page<LostBoardDto> fakeList = new PageImpl<>(boardList, pageable, boardList.size());


        given(boardService.boardList(any(Pageable.class))).willReturn(fakeList);

        mockMvc.perform(get("/board/list")
                        .session(session).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/boardList"))
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attributeExists("nowPage"))
                .andExpect(model().attributeExists("startPage"))
                .andExpect(model().attributeExists("endPage"))
                .andExpect(model().attributeExists("memberDto"));
    }

    @Test
    @DisplayName("게시판 목록 조회 list 속성이 존재하는 경우 모바일")
    void boardListTest_WithListAttributeM() throws Exception {

        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);

        List<LostBoardDto> boardList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        boardList.add(new LostBoardDto(1L, "제목", "내용", now));

        Pageable pageable = PageRequest.of(0, 10);
        Page<LostBoardDto> fakeList = new PageImpl<>(boardList, pageable, boardList.size());


        given(boardService.boardList(any(Pageable.class))).willReturn(fakeList);

        mockMvc.perform(get("/board/list")
                        .session(session).header("User-Agent", "Mozilla/5.0 (iPhone; CPU iP" +
                                "hone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)" +
                                " Version/15.0 Mobile/15E148 Safari/604.1"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/boardList"))
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attributeExists("nowPage"))
                .andExpect(model().attributeExists("startPage"))
                .andExpect(model().attributeExists("endPage"))
                .andExpect(model().attributeExists("memberDto"));
    }

    @Test
    @DisplayName("게시판 목록 조회 list 속성이 존재하는 경우 안드로이드")
    void boardListTest_WithListAttributeMA() throws Exception {

        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);

        List<LostBoardDto> boardList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        boardList.add(new LostBoardDto(1L, "제목", "내용", now));

        Pageable pageable = PageRequest.of(0, 10);
        Page<LostBoardDto> fakeList = new PageImpl<>(boardList, pageable, boardList.size());


        given(boardService.boardList(any(Pageable.class))).willReturn(fakeList);

        mockMvc.perform(get("/board/list")
                        .session(session).header("User-Agent", "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Mobile Safari/537.36"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/boardList"))
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attributeExists("nowPage"))
                .andExpect(model().attributeExists("startPage"))
                .andExpect(model().attributeExists("endPage"))
                .andExpect(model().attributeExists("memberDto"));
    }

    @Test
    @DisplayName("게시판 목록 조회 list 속성이 존재하는 경우 아이폰")
    void boardListTest_WithListAttributeMI() throws Exception {

        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);

        List<LostBoardDto> boardList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        boardList.add(new LostBoardDto(1L, "제목", "내용", now));

        Pageable pageable = PageRequest.of(0, 10);
        Page<LostBoardDto> fakeList = new PageImpl<>(boardList, pageable, boardList.size());


        given(boardService.boardList(any(Pageable.class))).willReturn(fakeList);

        mockMvc.perform(get("/board/list")
                        .session(session).header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605." +
                                "1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/" +
                                "604.1"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/boardList"))
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attributeExists("nowPage"))
                .andExpect(model().attributeExists("startPage"))
                .andExpect(model().attributeExists("endPage"))
                .andExpect(model().attributeExists("memberDto"));
    }

    @Test
    @DisplayName("게시판 목록 조회 검색어 포함 웹")
    void boardListWithSearchKeywordTestW() throws Exception {

        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);

        String searchKeyword = "검색어";


        List<LostBoardDto> boardList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        boardList.add(new LostBoardDto(1L, "제목", "내용", now));
        Pageable pageable = PageRequest.of(0, 10);
        Page<LostBoardDto> fakeList = new PageImpl<>(boardList, pageable, boardList.size());


        given(boardService.boardSearchList(eq(searchKeyword), any(Pageable.class))).willReturn(fakeList);

        mockMvc.perform(get("/board/list")
                        .session(session)
                        .param("searchKeyword", searchKeyword)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36")) // 검색어를 파라미터로 전달
                .andExpect(status().isOk())
                .andExpect(view().name("board/boardList"))
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attributeExists("nowPage"))
                .andExpect(model().attributeExists("startPage"))
                .andExpect(model().attributeExists("endPage"))
                .andExpect(model().attributeExists("memberDto"));
    }

    @Test
    @DisplayName("게시판 목록 조회 검색어 포함 모바일")
    void boardListWithSearchKeywordTestM() throws Exception {

        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);

        String searchKeyword = "검색어";


        List<LostBoardDto> boardList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        boardList.add(new LostBoardDto(1L, "제목", "내용", now));
        Pageable pageable = PageRequest.of(0, 10);
        Page<LostBoardDto> fakeList = new PageImpl<>(boardList, pageable, boardList.size());


        given(boardService.boardSearchList(eq(searchKeyword), any(Pageable.class))).willReturn(fakeList);

        mockMvc.perform(get("/board/list")
                        .session(session)
                        .param("searchKeyword", searchKeyword)
                        .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iP" +
                                "hone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)" +
                                " Version/15.0 Mobile/15E148 Safari/604.1")) // 검색어를 파라미터로 전달
                .andExpect(status().isOk())
                .andExpect(view().name("board/boardList"))
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attributeExists("nowPage"))
                .andExpect(model().attributeExists("startPage"))
                .andExpect(model().attributeExists("endPage"))
                .andExpect(model().attributeExists("memberDto"));
    }

    @Test
    @DisplayName("게시판 목록 조회 검색어 포함 안드로이드")
    void boardListWithSearchKeywordTestMA() throws Exception {

        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);

        String searchKeyword = "검색어";


        List<LostBoardDto> boardList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        boardList.add(new LostBoardDto(1L, "제목", "내용", now));
        Pageable pageable = PageRequest.of(0, 10);
        Page<LostBoardDto> fakeList = new PageImpl<>(boardList, pageable, boardList.size());


        given(boardService.boardSearchList(eq(searchKeyword), any(Pageable.class))).willReturn(fakeList);

        mockMvc.perform(get("/board/list")
                        .session(session)
                        .param("searchKeyword", searchKeyword)
                        .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Mobile Safari/537.36")) // 검색어를 파라미터로 전달
                .andExpect(status().isOk())
                .andExpect(view().name("board/boardList"))
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attributeExists("nowPage"))
                .andExpect(model().attributeExists("startPage"))
                .andExpect(model().attributeExists("endPage"))
                .andExpect(model().attributeExists("memberDto"));
    }

    @Test
    @DisplayName("게시판 목록 조회 검색어 포함 아이폰")
    void boardListWithSearchKeywordTestMI() throws Exception {

        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);

        String searchKeyword = "검색어";


        List<LostBoardDto> boardList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        boardList.add(new LostBoardDto(1L, "제목", "내용", now));
        Pageable pageable = PageRequest.of(0, 10);
        Page<LostBoardDto> fakeList = new PageImpl<>(boardList, pageable, boardList.size());


        given(boardService.boardSearchList(eq(searchKeyword), any(Pageable.class))).willReturn(fakeList);

        mockMvc.perform(get("/board/list")
                        .session(session)
                        .param("searchKeyword", searchKeyword)
                        .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPho" +
                                "ne OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Vers" +
                                "ion/15.0 Mobile/15E148 Safari/604.1")) // 검색어를 파라미터로 전달
                .andExpect(status().isOk())
                .andExpect(view().name("board/boardList"))
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attributeExists("nowPage"))
                .andExpect(model().attributeExists("startPage"))
                .andExpect(model().attributeExists("endPage"))
                .andExpect(model().attributeExists("memberDto"));
    }



    @Test
    @DisplayName("게시글 상세 조회")
    void boardViewTest() throws Exception {
        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);



        Long boardId = 1L;


        LocalDateTime now = LocalDateTime.now();
        LostBoardDto boardDto = new LostBoardDto(1L, "제목", "내용", now);
        given(boardService.boardView(boardId)).willReturn(boardDto);

        mockMvc.perform(get("/board/view")
                        .param("id", String.valueOf(1L)).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("board/boardview"))
                .andExpect(model().attributeExists("board"))
                .andExpect(model().attribute("board", boardDto));
    }


    @Test
    @DisplayName("게시글 상세 조회로그인안함")
    void boardViewTestNull() throws Exception {


        mockMvc.perform(get("/board/view")
                        .param("id", String.valueOf(1L)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/login"));
    }

    @Test
    @DisplayName("게시글 삭제 운영자")
    void boardDeleteTest_Admin() throws Exception {

        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.ADMIN, "123456789", Grade.ALPHA);


        session.setAttribute("loginMember", memberDto);


        Long boardId = 1L;

        mockMvc.perform(get("/board/delete")
                        .param("id", String.valueOf(boardId))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("SearchUrl"))
                .andExpect(view().name("board/Message"));

        verify(boardService).boardDelete(eq(boardId));
    }

    @Test
    @DisplayName("Board 쓰기 이미지없음")
    void boardWriteImageNo() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);
        session.setAttribute("loginMember", memberDto);

        doNothing().when(boardService).write(any(LostBoardDto.class), any(String.class), any(Long.class));

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/board/writepro")
                        .file(file)
                        .session(session)
                        .param("title", "제목")
                        .param("content", "내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
    }

    @Test
    @DisplayName("Board 쓰기 이미지있음")
    void boardWriteImage() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);
        session.setAttribute("loginMember", memberDto);

        doNothing().when(boardService).write(any(LostBoardDto.class), any(String.class), any(Long.class));

        Path imagePath = Paths.get("src/main/resources/static/assets/img/busAni.gif"); // 이미지 파일의 경로를 지정합니다
        String fileName = "image.png";
        String contentType = MediaType.IMAGE_PNG_VALUE;
        byte[] imageData = Files.readAllBytes(imagePath);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                contentType,
                imageData
        );

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/board/writepro")
                        .file(file)
                        .session(session)
                        .param("title", "제목")
                        .param("content", "내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
    }

    @Test
    @DisplayName("Board 업데이트 이미지없음")
    void boardUpdateImageNo() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);
        session.setAttribute("loginMember", memberDto);

        doNothing().when(boardService).write(any(LostBoardDto.class), any(String.class), any(Long.class));

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/board/update/1")
                        .file(file)
                        .session(session)
                        .param("title", "제목")
                        .param("content", "내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
    }

    @Test
    @DisplayName("Board 업데이트 이미지있음")
    void boardUpdateImage() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);
        session.setAttribute("loginMember", memberDto);

        doNothing().when(boardService).write(any(LostBoardDto.class), any(String.class), any(Long.class));

        Path imagePath = Paths.get("src/main/resources/static/assets/img/busAni.gif"); // 이미지 파일의 경로를 지정합니다
        String fileName = "image.png";
        String contentType = MediaType.IMAGE_PNG_VALUE;
        byte[] imageData = Files.readAllBytes(imagePath);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                contentType,
                imageData
        );
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/board/update/1")
                        .file(file)
                        .session(session)
                        .param("title", "제목")
                        .param("content", "내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
    }


    @Test
    @DisplayName("게시글 삭제 - 세션이 없는 경우")
    void boardDeleteTest_SessionExpired() throws Exception {


        mockMvc.perform(get("/board/delete")
                        .param("id", "1"))  // Assuming the board ID is 1
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/login"));
    }

    @Test
    @DisplayName("게시글 삭제 일반 사용자")
    void boardDeleteTest_User() throws Exception {

        MockHttpSession session = new MockHttpSession();

        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);

        session.setAttribute("loginMember", memberDto);

        mockMvc.perform(get("/board/delete")
                        .param("id", "1")  // Assuming the board ID is 1
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
    }

    @Test
    @DisplayName("게시글 수정 폼 운영자가 아닌 경우")
    void boardModifyFormRedirectToListWhenUserRoleIsNotAdmin() throws Exception {

        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);

        session.setAttribute("loginMember", memberDto);

        mockMvc.perform(get("/board/modify/1")  // Assuming the board ID is 1
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
    }

    @Test
    @DisplayName("게시글 수정 폼 운영자인경우")
    void boardModifyFormRedirectToListWhenUserRoleIsAdmin() throws Exception {

        MockHttpSession session = new MockHttpSession();


        MemberDto memberDto = new MemberDto(1L, "나", "123456789", "test@test.com", 0L,
                com.metanet.metabus.member.entity.Role.ADMIN, "123456789", Grade.ALPHA);
        LocalDateTime now = LocalDateTime.now();
        LostBoardDto board = new LostBoardDto(1L, "a", "a", now);

        session.setAttribute("loginMember", memberDto);
        given(boardService.boardView(1L)).willReturn(board);
        mockMvc.perform(get("/board/modify/1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("board/boardmodify"));
    }

    @Test
    @DisplayName("게시글 수정 폼 세션이 없는 경우")
    void boardModifyFormRedirectToLoginWhenSessionIsNull() throws Exception {
        mockMvc.perform(get("/board/modify/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/login"));
    }

    @Test
    @DisplayName("게시글 작성 성공")
    void boardWriteSuccess() throws Exception {

        MockHttpSession session = new MockHttpSession();
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);
        session.setAttribute("loginMember", memberDto);

        doNothing().when(boardService).write(any(LostBoardDto.class), any(String.class), any(Long.class));


        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(post("/board/writepro")
                        .session(session)
                        .param("title", "제목")
                        .param("content", "내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
    }


    @Test
    @DisplayName("게시글 수정 성공")
    void boardUpdateSuccess() throws Exception {

        MockHttpSession session = new MockHttpSession();
        MemberDto memberDto = new MemberDto(1L, "user", "password", "user@example.com", 0L,
                com.metanet.metabus.member.entity.Role.USER, "123456789", Grade.ALPHA);
        session.setAttribute("loginMember", memberDto);

        doNothing().when(boardService).write(any(LostBoardDto.class), any(String.class), any(Long.class));


        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/board/update/1")
                        .session(session)
                        .param("title", "제목")
                        .param("content", "내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
    }


}