package com.example.demo.domain.repository;

import com.example.demo.domain.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findAll();

    boolean existsByNumber(Long number);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Board b SET b.contents = :contents, b.filename = :filename, b.filesize =:filesize WHERE b.number =:number")
    Integer updateBoard(
            @Param("contents") String contents,
            @Param("filesize") String filesize,
            @Param("filename") String filename,
            @Param("number") Long number
    );

    @Query("SELECT b FROM Board b WHERE email = :email ORDER BY date DESC")
    List<Board> getBoardByEmailOrderByDateDesc(@Param("email") String email);

    @Query("SELECT b FROM Board b WHERE number = :number")
    List<Object> findByNumber(@Param("number") Long num);

    @Query("SELECT b FROM Board b WHERE number = :number")
    Optional<Board> findByNum(@Param("number") Long number);





}