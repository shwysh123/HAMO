package com.example.demo.restcontroller;

import com.example.demo.domain.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
public class CheckCodeRestController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private Model model;

    @PostMapping("/checkcode")
    public ResponseEntity<String> checkCode(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {

        String userEnteredCode = requestBody.get("code");

        String storedCode = (String) model.getAttribute("ePw");

        if (storedCode != null && storedCode.equals(userEnteredCode)) {

            return new ResponseEntity<>("authenticated", HttpStatus.OK);
        } else {

            return new ResponseEntity<>("not-authenticated", HttpStatus.OK);
        }
    }
}
