package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class SimpleController {
    @GetMapping("/member")
    public void member(){
        log.info("GET/member...");
    }
    @GetMapping("/admin")
    public void admin(){
        log.info("GET/admin...");
    }
    @GetMapping("/login")
    public void login(){
        log.info("GET/login...");
    }
    @GetMapping("/error")
    public void error(){
        log.info("GET/error...");
    }
    @GetMapping("/find")
    public void find(){
        log.info("GET/find...");
    }
}
