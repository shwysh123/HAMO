package com.example.demo.domain.service;

import com.example.demo.domain.dto.UserDto;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public boolean joinMember(UserDto dto, Model model, HttpServletRequest request) {
        //=======================================
        //Email 중복체크
        if (isEmailAlreadyTaken(dto.getEmail())) {
            model.addAttribute("email", "이미 사용중인 이메일입니다.");
            return false;
        }
        return false;
    }


        //=/===========================
        public boolean isEmailAlreadyTaken (String email){
            Optional<User> existingUser = userRepository.findByEmail(email);
            return existingUser.isPresent();
        }
        //=/===========================
    }



