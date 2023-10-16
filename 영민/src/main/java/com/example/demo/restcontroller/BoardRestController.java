package com.example.demo.restcontroller;

import com.example.demo.domain.dto.ReplyDto;
import com.example.demo.domain.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
@Slf4j
public class BoardRestController {

    @Autowired
    private BoardService boardService;

    //----------------------------------------------------------------
    //수정하기
    //----------------------------------------------------------------

    @PutMapping("/put/{number}/{filename}")
    public String put(@PathVariable String number, @PathVariable String filename)
    {
        log.info("PUT/board/put " + number + ""+ filename);
        boolean isremoved = boardService.removeFile(number,filename);
        return "success";
    }
    //----------------------------------------------------------------
    //삭제하기
    //----------------------------------------------------------------
    @DeleteMapping("/delete")
    public String delete(Long number) throws Exception{
        log.info("DELETE/board/delete number " + number);

        boolean isremoved = boardService.removeBoard(number);
        if(isremoved)
            return "success";
        else
            return "failed";
    }

    //--------
    //댓글추가
    //--------
    @GetMapping("/reply/add")
    public void addReply(Long bno,String content, String nickname){
        log.info("GET/board/reply/add " + bno + "" + content +"" + nickname);
        boardService.addReply(bno,content,nickname);
    }
    //---
    //댓글조회
    //---
    @GetMapping("/reply/list")
    public List<ReplyDto> getListReply(Long bno){
        log.info("GET/board/reply/list" + bno);
        List<ReplyDto> list = boardService.getReplyList(bno);
        return list;
    }

    //-------
    //댓글 카운트
    //-------
    @GetMapping("/reply/count")
    public Long getCount(Long bno){
        log.info("GET/board/reply/count " + bno);
        Long cnt = boardService.getReplyCount(bno);

        return cnt;
    }

    //----------------------------------
    //댓글 삭제
    //----------------------------------
    @GetMapping("/reply/delete")
    public void deleteReply(Long bno){
        log.info("GET/board/reply/delete " + bno);
        boardService.deleteReply(bno);
    }

    @GetMapping("/reply/edit")
    public void editReply(Long bno, String content){
        log.info("GET/board/reply/edit "+ bno + "" + content);
//        boardService.editReply(bno, content);
    }
}


