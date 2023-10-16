package com.example.demo.domain.repository;

import com.example.demo.domain.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findAll();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Board b SET b.contents = :contents, b.filename = :filename, b.filesize =:filesize WHERE b.number =:number")
    Integer updateBoard(
            @Param("contents") String contents,
            @Param("filesize") String filesize,
            @Param("filename") String filename,
            @Param("number") Long number
            );

    // Type , Keyword 로 필터링된 count 계산
    @Query("SELECT COUNT(b) FROM Board b WHERE b.contents LIKE %:keyWord%")
    Integer countWhereContentsKeyword(@Param("keyWord")String keyWord);

    @Query("SELECT COUNT(b) FROM Board b WHERE b.nickname LIKE %:keyWord%")
    Integer countWhereNicknameKeyword(@Param("keyWord")String keyWord);

    @Query(value = "SELECT * FROM sns.board b WHERE b.contents LIKE %:keyWord%  ORDER BY b.no DESC LIMIT :amount OFFSET :offset", nativeQuery = true)
    List<Board> findBoardContentsAmountStart(@Param("keyWord")String keyword, @Param("amount") int amount,@Param("offset") int offset);

    @Query(value = "SELECT * FROM sns.board b WHERE b.nickname LIKE %:keyWord%  ORDER BY b.no DESC LIMIT :amount OFFSET :offset", nativeQuery = true)
    List<Board> findBoardNicknameAmountStart(@Param("keyWord")String keyword, @Param("amount") int amount,@Param("offset") int offset);


}
