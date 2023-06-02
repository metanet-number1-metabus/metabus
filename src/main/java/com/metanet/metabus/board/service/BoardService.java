package com.metanet.metabus.board.service;

import com.metanet.metabus.board.dto.LostBoardDto;
import com.metanet.metabus.board.entity.LostBoard;
import com.metanet.metabus.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    private LostBoardDto lostBoardDto ;

    //글 작성 처리
    public void write(LostBoardDto board ,String fileName) throws IOException {

        /*디비에 파일 넣기*/
        board.setFilename(fileName);
        /*저장되는 경로*/
        /*저장된파일의이름,저장된파일의경로*/
        board.setFilepath("/files/" + fileName);

      LostBoard board1 = new LostBoard(board.getTitle(), board.getContent(),fileName,"/files/" + fileName);
        boardRepository.save(board1);

    }
    //게시글 리스트 처리
    public Page<LostBoardDto> boardList(Pageable pageable) {
        Page<LostBoardDto> boardPage = boardRepository.findAll(pageable).map(LostBoardDto::of);
        return boardPage;
    }

    public Page<LostBoardDto> boardSearchList(String searchKeyword, Pageable pageable) {
        Page<LostBoardDto> boardPage = boardRepository.findByTitleContaining(searchKeyword, pageable).map(LostBoardDto::of);
        return boardPage;
    }

    // 특정 게시글 불러오기
    public LostBoardDto boardView(Integer id) {
        Optional<LostBoard> boardOptional = boardRepository.findById(id);
        return boardOptional.map(LostBoardDto::of).orElse(null);
    }


    public void boardDelete(Integer id){
        boardRepository.deleteById(id);
    }


    public void update(LostBoardDto boardtemp, String fileName , Integer id) throws IOException {
        /*디비에 파일 넣기*/
        boardtemp.setFilename(fileName);
        /*저장되는 경로*/
        /*저장된파일의이름,저장된파일의경로*/
        boardtemp.setFilepath("/files/" + fileName);

        LostBoard board1 = new LostBoard(id,boardtemp.getTitle(), boardtemp.getContent(),fileName,"/files/" + fileName);
        boardRepository.save(board1);

    }
}
