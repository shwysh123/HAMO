package com.example.demo.domain.dto;

import lombok.Data;

@Data
public class Criteria {

    private int pageno;     // 현재페이지 필요없음
    private int amount;     // 표시할 게시물 양 필요없음
    private String type;    // 글내용, 작성자 검색할 조건
    private String keyword; // 포함 문자열

    public Criteria(){
        pageno = 1;
        amount = 10;
    }

    public Criteria (int no , int amt){
        pageno = no;
        amount = amt;
    }

}
