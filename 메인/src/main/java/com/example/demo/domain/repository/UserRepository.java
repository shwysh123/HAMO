package com.example.demo.domain.repository;

import com.example.demo.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {


    User findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    //----------------------------------------------------------------
    boolean existsByPhone(String value);

    //================================================================
    User findBynameAndPhoneAndQuestionAndAnswer(String name, String phone, String question, String answer);


}
