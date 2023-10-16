package com.example.demo.domain.service;

import com.example.demo.controller.BoardController;
import com.example.demo.domain.dto.BoardDto;
import com.example.demo.domain.dto.Criteria;
import com.example.demo.domain.dto.PageDto;
import com.example.demo.domain.dto.ReplyDto;
import com.example.demo.domain.entity.Board;
import com.example.demo.domain.entity.Reply;
import com.example.demo.domain.repository.BoardRepository;
import com.example.demo.domain.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;


@Service
public class BoardService {

    private String uploadDir = "c:\\upload";
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ReplyRepository replyRepository;

    //모든 게시물 가져오기

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> GetBoardList(Criteria criteria){

        Map<String, Object> returns = new HashMap <String,Object>();

        //전체게시물 건수 받기 (type, keyword가 적용된 count로 변경
        //--------------------------------------------------------
        //SEARCH
        //--------------------------------------------------------
        int totalcount = 0;
        if(criteria!=null&& criteria.getType()!=null){
            if(criteria.getType().equals("contents"))
                totalcount = boardRepository.countWhereContentsKeyword(criteria.getKeyword());
            else if(criteria.getType().equals("nickname"))
                totalcount = boardRepository.countWhereNicknameKeyword(criteria.getKeyword());
        }
    else
        totalcount = (int)boardRepository.count();

    System.out.println("COUNT : " + totalcount);

    //PageDto 만들기
        PageDto pagedto = new PageDto(totalcount,criteria);
    //시작 게시물 번호 구하기(수정) - OFFSET
    int offset = (criteria.getPageno()-1)*criteria.getAmount();         //1page = 0, 2page = 10
    //--------------------------------------------------------
    //SEARCH
    //--------------------------------------------------------
    List<Board> list = null;
    if(criteria!=null&& criteria.getType()!=null){
        if(criteria.getType().equals("contents")){
            list = boardRepository.findBoardContentsAmountStart(criteria.getKeyword(), pagedto.getCriteria().getAmount(),offset);
            System.out.println("CONTENTS SEARCH!");
            System.out.println(list);
        }else if (criteria.getType().equals("nickname"))
            list = boardRepository.findBoardNicknameAmountStart(criteria.getKeyword(), pagedto.getCriteria().getAmount(),offset);
    }

    returns.put("list",list);
    returns.put("pageDto", pagedto);

    return returns;
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean addBoard(BoardDto dto) throws IOException{

        System.out.println("upload File Count : "+ dto.getFiles().length);

        Board board =new Board();
        board.setContents(dto.getContents());
        board.setDate(LocalDateTime.now());
        board.setNickname(dto.getNickname());
        board.setLike_count(dto.getLike_count());
        board.setNumber(dto.getNumber());
        board.setHits(0L);

        MultipartFile [] files = dto.getFiles();

        List<String> filenames = new ArrayList<String>();
        List<String> filesizes = new ArrayList<String>();

        if(dto.getFiles().length >= 1 && dto.getFiles()[0].getSize()!=0L){
            //Upload Dir 미존재시 생성
            String path = uploadDir + File.separator + dto.getNickname() + File.separator + UUID.randomUUID();
            File dir = new File(path);
            if(!dir.exists()){
                dir.mkdirs();
            }
            //board에 경로 추가
            board.setDirpath(dir.toString());

            for(MultipartFile file : dto.getFiles()){
                System.out.println("--------------------");
                System.out.println("FILE NAME : "+file.getOriginalFilename());
                System.out.println("FILE SIZE : "+file.getSize() + "Byte");
                System.out.println("--------------------");

                //파일명 추출
                String filename = file.getOriginalFilename();
                //파일객체 생성
                File fileobj = new File(path,filename);
                //업로드
                file.transferTo(fileobj);
                //filenames 저장
                filenames.add(filename);
                filesizes.add(file.getSize()+"");
            }
            board.setFilename(filenames.toString());
            board.setFilesize(filesizes.toString());
        }
        board = boardRepository.save(board);

        boolean issaved = boardRepository.existsById(board.getNumber());
        return issaved;
    }
@Transactional(rollbackFor = Exception.class)
    public Board getBoardOne(Long number){
        Optional<Board> board = boardRepository.findById(number);
        if(board.isEmpty())
            return null;
        else
            return board.get();
    }
    //------------------------------------------------
    //수정하기
    //------------------------------------------------
    @Transactional(rollbackFor = SQLException.class)
    public boolean updateBoard(BoardDto dto) throws IOException{
        //------------------------------------------------
        System.out.println("upload FILE Count : "+ dto.getFiles().length);
        System.out.println("BoardController.READ_DIRECTORY_PATH : "+ BoardController.READ_DIRECTORY_PATH);

        Board board = new Board();
        board.setContents(dto.getContents());
        board.setDate(LocalDateTime.now());
        board.setNickname(dto.getNickname());
        board.setLike_count(dto.getLike_count());
        board.setNumber(dto.getNumber());
        board.setHits(dto.getHits());

        MultipartFile [] files = dto.getFiles();

        List<String> filenames = new ArrayList<String>();
        List<String> filesizes = new ArrayList<String>();

        //기존 정보 가져오기
        Board oldBoard = boardRepository.findById(dto.getNumber()).get();
        if(dto.getFiles().length >= 1 && dto.getFiles()[0].getSize()!=0L)
        {
            //-------------------------
            //Upload Dir 미존재시 생성[추가]
            //-------------------------
            if(BoardController.READ_DIRECTORY_PATH==null){
                String path =uploadDir + File.separator + dto.getNickname() + File.separator + UUID.randomUUID();
                File dir = new File(path);
                if(!dir.exists()){
                    dir.mkdirs();
                }
                BoardController.READ_DIRECTORY_PATH = path;
            }
            board.setDirpath(BoardController.READ_DIRECTORY_PATH.toString());

            for(MultipartFile file : dto.getFiles()){
                System.out.println("--------------------");
                System.out.println("FILE NAME : "+file.getOriginalFilename());
                System.out.println("FILE SIZE : "+file.getSize() + "Byte");
                System.out.println("--------------------");

                //파일명 추출
                String filename = file.getOriginalFilename();
                //파일객체 생성
                File fileobj = new File(BoardController.READ_DIRECTORY_PATH,filename);
                //업로드
                file.transferTo(fileobj);
                //filenames 저장
                filenames.add(filename);
                filesizes.add(file.getSize()+"");
        }
            //--------------------------------------------------------
            //기존 파일 정보와 병합
            //--------------------------------------------------------
            //코드 수정
            if(oldBoard.getFilename()!=null){
                String oldFilenames = oldBoard.getFilename();
                String oldFilesizes = oldBoard.getFilesize();
                String[] old_fn_arr = oldFilenames.split(",");
                String[] old_fs_arr = oldFilesizes.split(",");

            //첫문자열에 '[' 마지막 ']' 제거
                old_fn_arr[0] = old_fn_arr[0].substring(1, old_fn_arr[0].length());
                int lastIdx = old_fn_arr.length - 1;
                old_fn_arr[lastIdx] = old_fn_arr[lastIdx].substring(0, old_fn_arr[lastIdx].lastIndexOf("["));

                old_fs_arr[0] = old_fs_arr[0].substring(1,old_fs_arr[0].length());
                int lastIdx2 = old_fs_arr.length -1;
                old_fs_arr[lastIdx2] = old_fs_arr[lastIdx2].substring(0,old_fs_arr[lastIdx2].lastIndexOf("]"));

                String newFilenames[] = Stream.concat(Arrays.stream(old_fn_arr),Arrays.stream(filenames.toArray())).toArray(String[]::new);
                String newFilesizes[] = Stream.concat(Arrays.stream(old_fs_arr),Arrays.stream(filesizes.toArray())).toArray(String[]::new);
                System.out.println("newFilenames : "+(Arrays.toString(newFilenames)));
                System.out.println("newFilesizes : "+(Arrays.toString(newFilesizes)));

                board.setFilename(Arrays.toString(newFilenames));
                board.setFilesize(Arrays.toString(newFilesizes));
            }
            else{
                board.setFilename(filenames.toString());
                board.setFilesize(filesizes.toString());
            }
        }
        else{
            board.setFilename(oldBoard.getFilename());
            board.setFilesize(oldBoard.getFilesize());
        }
        board.setNumber(oldBoard.getNumber());

        board = boardRepository.save(board);

        return board != null;
    }
    @Transactional(rollbackFor = SQLException.class)
    public boolean removeFile(String number, String filename){
        Long num = Long.parseLong(number);
        filename = filename.trim();
        //----------------------------------------------------------------
        //파일삭제
        //----------------------------------------------------------------
        File file = new File(BoardController.READ_DIRECTORY_PATH,filename);
        if(!file.exists()){
            System.out.println("없음.."+ BoardController.READ_DIRECTORY_PATH);
            return false;
        }
        //---------------------------------
        //DB삭제
        //---------------------------------
        Board readBoard = boardRepository.findById(num).get();
        String fn [] = readBoard.getFilename().split(",");
        String fs [] = readBoard.getFilesize().split(",");

        //첫문자열에 '[' 마지막 ']' 제거
        fn[0] =fn[0].substring(1,fn[0].length());
        int lastIdx = fn.length -1;
        fn[lastIdx] =fn[lastIdx].substring(0,fn[lastIdx].lastIndexOf("["));

        fs[0] =fs[0].substring(1,fs[0].length());
        int lastIdx2 = fs.length -1;
        fs[lastIdx2] =fs[lastIdx2].substring(0,fs[lastIdx2].lastIndexOf("]"));

        List<String> newFn = new ArrayList();
        List<String> newFs = new ArrayList();

        for(int i=0; i<fn.length; i++)
        {
                if(!StringUtils.contains(fn[i],filename)){
                    System.out.println("보존 FILENAME: " + fn[i]);
                    newFn.add(fn[i]);
                    newFs.add(fs[i]);
                }
        }
        readBoard.setFilename(newFn.toString());
        readBoard.setFilesize(newFn.toString());
        readBoard = boardRepository.save(readBoard);

        return file.delete();
    }

    @Transactional(rollbackFor = SQLException.class)
    public boolean removeBoard(Long number){
        Board board = boardRepository.findById(number).get();
        String removePath = BoardController.READ_DIRECTORY_PATH;

        //파일 있으면 삭제
        File dir = new File(removePath);
        if(dir.exists()){
            File files[] = dir.listFiles();
            for(File file : files){
                file.delete();
            }
            dir.delete();
        }
        // DB삭제
        boardRepository.delete(board);

        return boardRepository.existsById(number);
    }
    //----------------------
    //COUNT
    //----------------------
    @Transactional(rollbackFor = SQLException.class)
    public void count(Long number){
        Board board = boardRepository.findById(number).get();
        board.setHits(board.getHits() + 1);
        boardRepository.save(board);
   }
   //--------------------
    //REPLY ADD
    //--------------------
    public void addReply(Long bno, String content, String nickname){

        Reply reply = new Reply();
        Board board = new Board();
        board.setNumber(bno);

        reply.setBoard(board);
        reply.setContent(content);
        reply.setNickname(nickname);
        reply.setDate(LocalDateTime.now());

        replyRepository.save(reply);
    }

    //-----------------------
    //Reply list
    //-----------------------
    public List<ReplyDto> getReplyList(Long bno){
        List<Reply> replyList =  replyRepository.GetReplyByBnoDesc(bno);

        List<ReplyDto> returnReply = new ArrayList();
        ReplyDto dto = null;

        if(!replyList.isEmpty());
        {
            for(int i= 0; i<replyList.size();i++){

                dto = new ReplyDto();
                dto.setBno(replyList.get(i).getBoard().getNumber());
                dto.setRnumber(replyList.get(i).getRnumber());
                dto.setNickname(replyList.get(i).getNickname());
                dto.setContent(replyList.get(i).getContent());
                dto.setDate(replyList.get(i).getDate());

                returnReply.add(dto);
        }
            return returnReply;
        }
    }
    //-----------------------------
    //Reply count by Bno
    //-----------------------------

    public Long getReplyCount(Long bno) {
//        return replyRepository.GetReplyCountByBnoDesc(bno);
    return null;
    }

    public void deleteReply(Long rnumber){

        replyRepository.deleteById(rnumber);
    }
//    public void editReply(Long rnumber, String content){
////        replyRepository.(rnumber);
    }



