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
    public void write(LostBoardDto board , MultipartFile file) throws IOException {

        /*우리의 프로젝트경로를 담아주게 된다 - 저장할 경로를 지정*/
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";

        /*식별자 . 랜덤으로 이름 만들어줌*/
        UUID uuid = UUID.randomUUID();

        /*랜덤식별자_원래파일이름 = 저장될 파일이름 지정*/
        String fileName = uuid+".png";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        /*빈 껍데기 생성*/
        /*File을 생성할건데, 이름은 "name" 으로할거고, projectPath 라는 경로에 담긴다는 뜻*/
        File saveFile = new File(projectPath, encodedFileName);
        file.transferTo(saveFile);


      LostBoard board1 = new LostBoard(board.getTitle(), board.getContent(),encodedFileName,"/files/" + encodedFileName);
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


    public void update(LostBoardDto boardtemp, MultipartFile file) throws IOException {
        /*우리의 프로젝트경로를 담아주게 된다 - 저장할 경로를 지정*/
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";

        /*식별자 . 랜덤으로 이름 만들어줌*/
        UUID uuid = UUID.randomUUID();

        /*랜덤식별자_원래파일이름 = 저장될 파일이름 지정*/
        String fileName = uuid+".png";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        /*빈 껍데기 생성*/
        /*File을 생성할건데, 이름은 "name" 으로할거고, projectPath 라는 경로에 담긴다는 뜻*/
        File saveFile = new File(projectPath, encodedFileName);
        file.transferTo(saveFile);

        LostBoard board = new LostBoard(boardtemp.getTitle(), boardtemp.getContent(),encodedFileName,"/files/" + encodedFileName);

        boardRepository.save(board);
    }
}
