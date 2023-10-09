package com.main.board.controller;

import com.main.board.dto.BoardDTO;
import com.main.board.dto.CommentDTO;
import com.main.board.service.BoardService;
import com.main.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;


    @GetMapping("/save")
    public String saveForm() {
        return "save2";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        System.out.println("boardDTO = " + boardDTO);
        boardService.save(boardDTO);
        return "index";
    }

    @GetMapping("/")
    public String findAll(Model model) {
        List<BoardDTO> boardDTOList = boardService.findALl();
        model.addAttribute("boardList",boardDTOList);
        return "list2";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model,
                           @PageableDefault(page=1) Pageable pageable){
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        //댓글목록 가져오기
        List<CommentDTO> commentDTOList = commentService.findAll(id);
        model.addAttribute("commentList",commentDTOList);
        model.addAttribute("board",boardDTO);
        model.addAttribute("page",pageable.getPageNumber());

        return "detail2";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model){
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate",boardDTO);
        return "update2";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model){
        BoardDTO board = boardService.update(boardDTO);
        model.addAttribute("board",board);
        return "detail2";
//        return "redirect:/board/"+boardDTO.getId();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        boardService.delete(id);
        return "redirect:/board/";
    }

    @GetMapping("/paging")
    public String paging(@PageableDefault(page=1) Pageable pageable, Model model) {
//        pageable.getPageNumber();
        Page<BoardDTO> boardList = boardService.paging(pageable);
        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();
        model.addAttribute("boardList",boardList);
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);
        return "paging";

    }
}
