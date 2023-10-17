package com.example.demo.controller;

import com.example.demo.domain.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmailController {

    private EmailService emailService;

    @PostMapping("/sendemail")
    @ResponseBody
    public String sendemail(@RequestParam("email") String email) throws Exception{
        EmailService registerMail = null;

        String code = registerMail.sendSimpleMessage(email);
        System.out.println("인증코드 : " + code);
        return code;
    }
}