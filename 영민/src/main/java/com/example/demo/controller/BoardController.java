package com.example.demo.controller;

import com.example.demo.domain.dto.BoardDto;
import com.example.demo.domain.dto.Criteria;
import com.example.demo.domain.dto.UserDto;
import com.example.demo.domain.entity.Board;
import com.example.demo.domain.dto.PageDto;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.BoardRepository;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.domain.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.naming.Binding;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    public static String READ_DIRECTORY_PATH;

    //----------------------------------------------------------------
    @GetMapping("/list")
    public String list(Model model, HttpServletResponse response) {
        log.info("GET/list...");

        //로그인 이메일 가져오기      ?? 필요성
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        //UserRepository 사용 정보 가져오기
        User user = userRepository.findByEmail(email).get();

        //UserDto 객체 생성
        User dto = UserDto.dtoToEntity(user);
        //사용자 정보에서 닉네임을 가져와서 설정
        dto.setNickname(user.getNickname());

        model.addAttribute("dto",dto);

        List<Board> list = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
        model.addAttribute("board", list);

        return list.toString();
    }


    //----------------------------------------------------
    //POST
    //----------------------------------------------------
    @GetMapping("/post")
    public void get_addBoard() {
        log.info("GET/post");
    }

    @PostMapping("/post")
    public String post_addBoard(@Valid BoardDto dto, BindingResult bindingResult, Model model) throws
            IOException {
        log.info("GET/post " + dto + "" + dto);

        //유효성 검사
        if (bindingResult.hasFieldErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                log.info(error.getField() + ":" + error.getDefaultMessage());
                model.addAttribute(error.getField(), error.getDefaultMessage());
            }
            return "/post";
        }
        //서비스 실행
        boolean isadd = boardService.addBoard(dto);
        if (isadd) {
            return "redirect:/list";
        }
        return "redirect:/post";
    }
    //--------------------
    //READ
    //--------------------

    @GetMapping("/read")
    public String read(Long number, Integer pageNo, Model model, HttpServletRequest request, HttpServletResponse response) {
        log.info("GET/read : " + number);

        //서비스실행
        Board board = boardService.getBoardOne(number);
        BoardDto dto = new BoardDto();
        dto.setNumber(board.getNumber());
        dto.setNickname(board.getNickname());
        dto.setContents(board.getContents());
        dto.setDate(board.getDate());
        dto.setHits(board.getHits());
        dto.setLike_count(board.getLike_count());

        System.out.println("FILENAMES : " + board.getFilename());
        System.out.println("FILESIZES : " + board.getFilesize());

        String filenames[] = null;
        String filesizes[] = null;
        if (board.getFilename() != null) {
            //첫문자열에 [ 제거
            filenames = board.getFilename().split(",");
            filenames[0] = filenames[0].substring(1, filenames[0].length());
            //마지막문자열에 ] 제거
            int lastIdx = filenames.length - 1;
            System.out.println("filenames[lastIdx] : " + filenames[lastIdx].substring(0, filenames[lastIdx].lastIndexOf("]")));
            filenames[lastIdx] = filenames[lastIdx].substring(0, filenames[lastIdx].lastIndexOf("]"));

            model.addAttribute("filenames", filenames);
        }
        if(board.getFilesize()!=null){
            //첫문자열에 [ 제거
            filesizes = board.getFilesize().split(",");
            filesizes[0] = filesizes[0].substring(1, filesizes[0].length());
            //마지막 문자열에 ] 제거
            int lastIdx = filesizes.length-1;
            System.out.println("filesizes[lastIdx] : " + filesizes[lastIdx].substring(0,filesizes[lastIdx].lastIndexOf("]")));
            filesizes[lastIdx] = filesizes[lastIdx].substring(0,filesizes[lastIdx].lastIndexOf("]"));



            model.addAttribute("filesizes", filesizes);
        }
        if(board.getDirpath()!=null){
            //--------------------------------------------------------
            // FILEDOWNLOAD 추가
            //--------------------------------------------------------
            this.READ_DIRECTORY_PATH = board.getDirpath();
        }
        model.addAttribute("boardDto",dto);
        model.addAttribute("pageNo",pageNo);

        //--------------------------------------------------------
        //COUNTUP
        //--------------------------------------------------------

        //쿠키 확인 후 COUNTUP(/board/read.do 새로고침시 조회수 반복증가를 막기위한용도
        Cookie[] cookies = request.getCookies();
        if(cookies!= null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("reading")){
                    if(cookie.getValue().equals("true")){
                        //CountUp
                        System.out.println("COOKIE READING TRUE | COUNT UP");
                        boardService.count(board.getNumber());
                        //쿠키 value 변경
                        cookie.setValue("false");
                        response.addCookie(cookie);
                    }
                }
            }
        }
        return "read";
        }
        @GetMapping("/update")
    public void update(Long number,Model model){
        log.info("GET/update number "+ number);
        //서비스 실행
            Board board = boardService.getBoardOne(number);
        BoardDto dto = new BoardDto();
        dto.setNumber(board.getNumber());
        dto.setContents(board.getContents());
        dto.setHits(board.getHits());
        dto.setDate(board.getDate());
        dto.setNickname(board.getNickname());
        dto.setLike_count(board.getLike_count());

        System.out.println("FILENAMES : "+ board.getFilename());
        System.out.println("FILESIZES : "+ board.getFilesize());

        String filenames[] = null;
        String filesizes[] = null;
        if(board.getFilename()!=null){
            //첫문자열에 [ 제거
            filenames = board.getFilename().split(",");
            filenames[0] = filenames[0].substring(1, filenames[0].length());
            //마지막 문자열에 ] 제거
            int lastIdx = filenames.length-1;
            System.out.println("filenames[lastIdx] : " + filenames[lastIdx].substring(0,filenames[lastIdx].lastIndexOf("]")));
            filenames[lastIdx] = filenames[lastIdx].substring(0,filenames[lastIdx].lastIndexOf("]"));

            model.addAttribute("filenames", filenames);
        }
            if(board.getFilesize()!=null){
                //첫문자열에 [ 제거
                filesizes = board.getFilesize().split(",");
                filesizes[0] = filesizes[0].substring(1, filesizes[0].length());
                //마지막 문자열에 ] 제거
                int lastIdx = filesizes.length-1;
                System.out.println("filesizes[lastIdx] : " + filesizes[lastIdx].substring(0,filesizes[lastIdx].lastIndexOf("]")));
                filesizes[lastIdx] = filesizes[lastIdx].substring(0,filesizes[lastIdx].lastIndexOf("]"));



                model.addAttribute("filesizes", filesizes);
            }
            if(board.getDirpath()!=null){
                this.READ_DIRECTORY_PATH = board.getDirpath();
        }
            model.addAttribute("boardDto", dto);
        }
        @PostMapping("/update")
    public String Post_update(@Valid BoardDto dto, BindingResult bindingResult, Model model) throws IOException{
        log.info("POST/update dto " +dto);

        if(bindingResult.hasFieldErrors()){
            for(FieldError error : bindingResult.getFieldErrors()){
                log.info(error.getField()+":"+ error.getDefaultMessage());
                model.addAttribute(error.getField(),error.getDefaultMessage());
            }
            return "read";
        }
        //서비스 실행
            boolean isadd = boardService.updateBoard(dto);

        if(isadd){
            return "redirect:read?number="+dto.getNumber();
        }
        return "redirect:update?number="+dto.getNumber();
        }
        //----------------------
        // /Board/reply/delete
        //----------------------

    @GetMapping("/reply/delete/{bno}/{rnumber}")
    public String delete(@PathVariable Long bno, @PathVariable Long rnumber){
        log.info("GET/board/reply/delete bno,number "+ bno+ "" + rnumber);

        boardService.deleteReply(rnumber);

        return "redirect:/read?number="+bno;
    }
    @ExceptionHandler(Exception.class)
    public String error1(Exception ex, Model model){
        System.out.println("BoardExceptionHandler FileNotFoundException... ex"+ ex);
        model.addAttribute("ex",ex);
        return "error";
    }
    }
