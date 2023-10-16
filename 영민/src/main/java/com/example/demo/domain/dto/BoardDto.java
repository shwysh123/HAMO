package com.example.demo.domain.dto;

import com.example.demo.domain.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {

    private Long number;
    private String nickname;
    private String contents;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private Long hits;
    private Long like_count;
    private MultipartFile[] files;
    private String email;

    public static BoardDto Of(Board board) {
        BoardDto dto = new BoardDto();
        dto.number = board.getNumber();
        dto.contents = board.getContents();
        dto.date = board.getDate();
        dto.nickname = board.getNickname();
        dto.hits = board.getHits();
        dto.like_count = board.getLike_count();
        dto.email = board.getEmail();
        return dto;
    }
}
