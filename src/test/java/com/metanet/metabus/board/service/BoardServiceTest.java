package com.metanet.metabus.board.service;

import com.metanet.metabus.board.dto.LostBoardDto;
import com.metanet.metabus.board.entity.LostBoard;
import com.metanet.metabus.board.repository.BoardRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BoardServiceTest {
    private final BoardRepository boardRepository = mock(BoardRepository.class);
    private final BoardService boardService = new BoardService(boardRepository);


    @Test
    @DisplayName("게시글 작성")
    void write() throws IOException {

        LostBoardDto boardDto = new LostBoardDto(1L,"제목","내용", LocalDateTime.now());
        String fileName = "example.jpg";
        Long memId = 1L;


        String expectedFilePath = "/files/" + fileName;


        Assertions.assertDoesNotThrow(() -> boardService.write(boardDto, fileName, memId));


        assertEquals(fileName, boardDto.getFilename());
        assertEquals(expectedFilePath, boardDto.getFilepath());
    }

    @Test
    @DisplayName("게시글 업데이트")
    void updateLostBoard() throws IOException {

        LostBoardDto boardDto = new LostBoardDto(1L,"제목","내용", LocalDateTime.now());
        String fileName = "example.jpg";
        Long memId = 1L;


        String expectedFilePath = "/files/" + fileName;


        Assertions.assertDoesNotThrow(() -> boardService.update(boardDto, fileName, memId));


        assertEquals(fileName, boardDto.getFilename());
        assertEquals(expectedFilePath, boardDto.getFilepath());
    }

    @Test
    @DisplayName("게시글 보기")
    void boardView() {

        Long boardId = 1L;
        LostBoard board = new LostBoard(boardId, "Title", "Content", "filename", "filepath", LocalDateTime.now(), 1L);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        LostBoardDto result = boardService.boardView(boardId);

        assertEquals(boardId, result.getId());
        assertEquals(board.getTitle(), result.getTitle());
        assertEquals(board.getContent(), result.getContent());
        assertEquals(board.getFilename(), result.getFilename());
        assertEquals(board.getFilepath(), result.getFilepath());
    }

    @Test
    @DisplayName("게시글 삭제")
    void boardDelete() {

        Long boardId = 1L;
        LostBoard board = new LostBoard(boardId, "Title", "Content", "filename", "filepath", LocalDateTime.now(), 1L);


        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        doNothing().when(boardRepository).deleteById(boardId);


        boardService.boardDelete(boardId);

    }

    @Test
    @DisplayName("게시판 리스트")
    void boardList() {

        PageRequest pageable = PageRequest.of(0, 10);

        List<LostBoard> boardList = new ArrayList<>();
        for(int i = 1 ;i<11;i++){
            boardList.add(new LostBoard((long) i, "Title 1", "Content 1",
                    "File 1", "/files/1", LocalDateTime.now(), 1L));
        }

        Page<LostBoard> mockedPage = new PageImpl<>(boardList, pageable, boardList.size());
        when(boardRepository.findByDeletedDateIsNull(pageable)).thenReturn(mockedPage);


        Page<LostBoardDto> resultPage = boardService.boardList(pageable);


        assertEquals(boardList.size(), resultPage.getSize());

    }

    @Test
    @DisplayName("게시판 검색 리스트")
    void boardSearchList() {

        String searchKeyword = "keyword";
        PageRequest pageable = PageRequest.of(0, 10);

        List<LostBoard> boardList = new ArrayList<>();
        for(int i = 1 ;i<11;i++){
            boardList.add(new LostBoard((long) i, "Title 1", "Content 1",
                    "File 1", "/files/1", LocalDateTime.now(), 1L));
        }

        Page<LostBoard> mockedPage = new PageImpl<>(boardList, pageable, boardList.size());
        when(boardRepository.findByTitleContainingAndDeletedDateIsNull(searchKeyword, pageable)).thenReturn(mockedPage);

        Page<LostBoardDto> resultPage = boardService.boardSearchList(searchKeyword, pageable);

        assertEquals(boardList.size(), resultPage.getSize());

    }


}