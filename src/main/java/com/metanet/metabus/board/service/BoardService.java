package com.metanet.metabus.board.service;

import com.metanet.metabus.board.dto.LostBoardDto;
import com.metanet.metabus.board.entity.LostBoard;
import com.metanet.metabus.board.repository.BoardRepository;
import com.metanet.metabus.member.dto.MemberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    private LostBoardDto lostBoardDto ;

    //글 작성 처리
    public void write(LostBoardDto board ,String fileName,Long memid) throws IOException {

        /*디비에 파일 넣기*/
        board.setFilename(fileName);
        /*저장되는 경로*/
        /*저장된파일의이름,저장된파일의경로*/
        board.setFilepath("/files/" + fileName);

      LostBoard board1 = new LostBoard(board.getTitle(), board.getContent(),fileName,"/files/" + fileName, LocalDateTime.now(), memid);
        boardRepository.save(board1);

    }
    //게시글 리스트 처리
    public Page<LostBoardDto> boardList(Pageable pageable) {
        Page<LostBoardDto> boardPage = boardRepository.findByDeletedDateIsNull(pageable)
                .map(LostBoardDto::of);
        return boardPage;
    }

    public Page<LostBoardDto> boardSearchList(String searchKeyword, Pageable pageable) {
        Page<LostBoardDto> boardPage = boardRepository.findByTitleContainingAndDeletedDateIsNull(searchKeyword, pageable)
                .map(LostBoardDto::of);
        return boardPage;
    }

    // 특정 게시글 불러오기
    public LostBoardDto boardView(Long id) {
        Optional<LostBoard> boardOptional = boardRepository.findById(id);
        return boardOptional.map(LostBoardDto::of).orElse(null);
    }


    public void boardDelete(Long id){
        LostBoard board = boardRepository.findById(id).orElseThrow();
        LostBoard board1 = new LostBoard(id,board.getTitle(), board.getContent(),board.getFilename(),board.getFilepath(),LocalDateTime.now(),board.getMemberId());
        boardRepository.save(board1);
    }


    public void update(LostBoardDto boardtemp, String fileName , Long id) throws IOException {
        /*디비에 파일 넣기*/
        boardtemp.setFilename(fileName);
        /*저장되는 경로*/
        /*저장된파일의이름,저장된파일의경로*/
        boardtemp.setFilepath("/files/" + fileName);

        LostBoard board1 = new LostBoard(id,boardtemp.getTitle(), boardtemp.getContent(),fileName,"/files/" + fileName);
        boardRepository.save(board1);

    }
}
