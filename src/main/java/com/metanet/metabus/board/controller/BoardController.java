package com.metanet.metabus.board.controller;


import com.metanet.metabus.board.dto.LostBoardDto;
import com.metanet.metabus.board.service.AwsS3Service;
import com.metanet.metabus.board.service.BoardService;
import com.metanet.metabus.board.service.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class BoardController {


    @Autowired
    private BoardService boardService;

    @Autowired
    private final AwsS3Service awsS3Service;

    public BoardController(AwsS3Service awsS3Service) {
        this.awsS3Service = awsS3Service;
    }


    @GetMapping("/board/write") //localhost:8090/board/write
    public String boardwriteForm() {

        return "/board/boardwrite";
    }

    @PostMapping("/board/writepro")
    public String boardWritePro(LostBoardDto board, Model model, MultipartFile[] file) throws IOException {
        model.addAttribute("message", "글 작성이 완료 되었습니다.");
        model.addAttribute("SearchUrl", "/board/list");
        /*식별자 . 랜덤으로 이름 만들어줌*/
        UUID uuid = UUID.randomUUID();

        /*랜덤식별자_원래파일이름 = 저장될 파일이름 지정*/
        String fileName = uuid + ".png";

        if(file!=null) {
            File mergedImageFile = ImageUtils.mergeImagesVertically(file);
            awsS3Service.upload(mergedImageFile, "upload", fileName);
        }
        boardService.write(board, fileName);

        return "redirect:/board/list";
    }


    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword) {

        Page<LostBoardDto> list = null;

        if (searchKeyword == null) {
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "/board/boardlist";

    }

    @GetMapping("/board/view")
    public String boardView(Model model, Long id) {
        LostBoardDto boardDto = boardService.boardView(id);

        model.addAttribute("board", boardDto);
        return "/board/boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Long id, Model model) {
        boardService.boardDelete(id);

        model.addAttribute("message", "글 삭제 완료.");
        model.addAttribute("SearchUrl", "/board/list");

        return "/board/Message";
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Long id, Model model) {
        model.addAttribute("board", boardService.boardView(id));

        return "/board/boardmodify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(LostBoardDto board, Model model,@PathVariable("id") Long id,MultipartFile[] file) throws IOException {


        model.addAttribute("message", "글 수정 완료.");
        model.addAttribute("SearchUrl", "/board/list");
        /*식별자 . 랜덤으로 이름 만들어줌*/
        UUID uuid = UUID.randomUUID();

        /*랜덤식별자_원래파일이름 = 저장될 파일이름 지정*/
        String fileName = uuid + ".png";

        if(file!=null) {
            File mergedImageFile = ImageUtils.mergeImagesVertically(file);
            awsS3Service.upload(mergedImageFile, "upload", fileName);
        }
        boardService.update(board, fileName,id);

        return "redirect:/board/list";
    }
}
