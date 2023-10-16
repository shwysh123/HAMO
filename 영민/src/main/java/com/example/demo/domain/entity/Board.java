package com.example.demo.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long number;
    private String nickname; // user에 nname 외래키
    private String contents;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 유동적으로 바뀌게
    private LocalDateTime date;
    private Long hits;      //조회수 1000넘어가면 K 10000넘어가면 M
    private Long like_count;  //좋아요 갯수 1000개 넘어가면 K 10000개넘어가면 M

    private String dirpath; //필요여부 확인 불필요한 데이터량 감소  저장공간이 게시물에 표시안되고 필요가없는듯
    private String filename; //필요여부 확인 불필요한 데이터량 감소  위와 같음
    private String filesize; //필요여부 확인 불필요한 데이터량 감소 위와 같음 어짜피 올릴수있는 용량은 정해져있어서

    private String email; // user와 연결지점 user가 탈퇴하면 user의 게시물 삭제
}
