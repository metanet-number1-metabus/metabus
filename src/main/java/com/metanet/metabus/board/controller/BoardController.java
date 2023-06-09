package com.metanet.metabus.board.controller;


import com.metanet.metabus.board.dto.LostBoardDto;
import com.metanet.metabus.board.service.AwsS3Service;
import com.metanet.metabus.board.service.BoardService;
import com.metanet.metabus.board.service.ImageUtils;
import com.metanet.metabus.common.exception.unauthorized.invalidSession;
import com.metanet.metabus.member.controller.SessionConst;
import com.metanet.metabus.member.dto.MemberDto;
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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
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


    @GetMapping("/board/write")
    public String boardwriteForm(HttpSession session) {
        MemberDto memberDto = (MemberDto)session.getAttribute("loginMember");
        if(memberDto==null){
            return "redirect:/member/login";
        }
        if(!memberDto.getRole().name().equals("ADMIN")){
            invalidSession invalidSession = new invalidSession();
            return "redirect:/board/list";
        }

        return "board/boardwrite";
    }

    @PostMapping("/board/writepro")
    public String boardWritePro(LostBoardDto board, Model model, MultipartFile[] file, HttpSession session) throws IOException {
        MemberDto memberDto = (MemberDto)session.getAttribute("loginMember");
        if(memberDto==null){
            return "redirect:/member/login";
        }
        if(!memberDto.getRole().name().equals("ADMIN")){
            invalidSession invalidSession = new invalidSession();
            return "redirect:/board/list";
        }

        if(board.getTitle().equals("")){
            return "redirect:/board/list";
        }
        model.addAttribute("message", "글 작성이 완료 되었습니다.");
        model.addAttribute("SearchUrl", "/board/list");
        /*식별자 . 랜덤으로 이름 만들어줌*/
        UUID uuid = UUID.randomUUID();

        /*랜덤식별자_원래파일이름 = 저장될 파일이름 지정*/
        String fileName = uuid + ".png";



        if (file != null && file.length > 0) {
            boolean check = false;
            for (MultipartFile uploadedFile : file) {
                if (ImageUtils.isImageFile(uploadedFile)) { // 이미지 파일인지 확인
                    check = true;
                }else{
                    check = false;
                    break;
                }
            }
            if(check==true){
                File mergedImageFile = ImageUtils.mergeImagesVertically(file);
                awsS3Service.upload(mergedImageFile, "upload", fileName);
            }
        }
        boardService.write(board, fileName,memberDto.getId());

        return "redirect:/board/list";
    }


    @GetMapping("/board/list")
    public String boardList(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) MemberDto memberDto, Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword) {



        Page<LostBoardDto> list = null;

        if (searchKeyword == null) {
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword, pageable);
        }


        int nowPage = list.getPageable().getPageNumber() + 1;
        int endPage = Math.min(nowPage + 4, list.getTotalPages());
        int startPage = Math.max(endPage-4, 1);



            model.addAttribute("list", list);
            model.addAttribute("nowPage", nowPage);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            model.addAttribute("memberDto", memberDto);



        return "board/boardlist";

    }

    @GetMapping("/board/view")
    public String boardView(Model model, Long id) {
        LostBoardDto boardDto = boardService.boardView(id);

        model.addAttribute("board", boardDto);
        return "board/boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Long id, Model model,HttpSession session) {
        MemberDto memberDto = (MemberDto)session.getAttribute("loginMember");
        if(memberDto==null){
            return "redirect:/member/login";
        }
        if(!memberDto.getRole().name().equals("ADMIN")){
            invalidSession invalidSession = new invalidSession();
            return "redirect:/board/list";
        }
        boardService.boardDelete(id);

        model.addAttribute("message", "글 삭제 완료.");
        model.addAttribute("SearchUrl", "/board/list");

        return "board/Message";
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Long id, Model model,HttpSession session) {
        MemberDto memberDto = (MemberDto)session.getAttribute("loginMember");
        if(memberDto==null){
            return "redirect:/member/login";
        }
        if(!memberDto.getRole().name().equals("ADMIN")){
            invalidSession invalidSession = new invalidSession();
            return "redirect:/board/list";
        }
        model.addAttribute("board", boardService.boardView(id));

        return "board/boardmodify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(LostBoardDto board, Model model,@PathVariable("id") Long id,MultipartFile[] file,HttpSession session) throws IOException {
        MemberDto memberDto = (MemberDto)session.getAttribute("loginMember");
        if(memberDto==null){
            return "redirect:/member/login";
        }
        if(!memberDto.getRole().name().equals("ADMIN")){
            invalidSession invalidSession = new invalidSession();
            return "redirect:/board/list";
        }
        if(board.getTitle().equals("")){
            return "redirect:/board/list";
        }

        model.addAttribute("message", "글 수정 완료.");
        model.addAttribute("SearchUrl", "/board/list");
        /*식별자 . 랜덤으로 이름 만들어줌*/
        UUID uuid = UUID.randomUUID();

        /*랜덤식별자_원래파일이름 = 저장될 파일이름 지정*/
        String fileName = uuid + ".png";

        if (file != null && file.length > 0) {
            boolean check = false;
            for (MultipartFile uploadedFile : file) {
                if (ImageUtils.isImageFile(uploadedFile)) { // 이미지 파일인지 확인
                    check = true;
                }else{
                    check = false;
                    break;
                }
            }
            if(check==true){
                File mergedImageFile = ImageUtils.mergeImagesVertically(file);
                awsS3Service.upload(mergedImageFile, "upload", fileName);
            }
        }
        boardService.update(board, fileName,id);

        return "redirect:/board/list";
    }
}
