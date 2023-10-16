package com.example.demo.restcontroller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordRestController {
    @PostMapping("/resetpw")
    public String resetPassword(String password, String passwordConfirm){
        if(password.equals(passwordConfirm)){

            return "redirect:/login";
        } else {
            return ("비밀번호가 일치하지 않습니다.");
        }
    }
}
