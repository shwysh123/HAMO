package com.example.demo.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
public class EmailService implements EmailServiceHelp {

    @Autowired
    JavaMailSender emailsender;

    private String ePw;

    // 메일 내용 작성
    @Override
    public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {


        MimeMessage message = emailsender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("하모하모 비밀번호 재설정 이메일 인증");

        String msgg =
          "<div style='margin:100px;'>"
         + "<h1> 안녕하세요!</h1>"
         + "<h1> 하모하모 입니다.</h1>"
         + "<br>"
         + "<p>아래 코드를 인증번호 입력란에 입력해주세요<p>"
         + "<br>"
         + "<p>감사합니다.<p>"
         + "<br>"
         + "<div align='center' style='border:1px solid black; font-family:verdana';>"
         + "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>"
         + "<div style='font-size:130%'>"
         + "CODE : <strong>"
         + ePw + "</strong><div><br/> "
         + "</div>";

        message.setText(msgg, "utf-8", "html");

        message.setFrom(new InternetAddress("HAMOHAMO@HAMO.com", "HAMOHAMO_Admin"));

        return message;
    }

    @Override
    public String createKey() {

        Random rnd = new Random();
        StringBuffer key = new StringBuffer();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        for (int i = 0; i < 8; i++) {
            int index = rnd.nextInt(characters.length());
            key.append(characters.charAt(index));
        }

        return key.toString();
    }

    @Override
    public String sendSimpleMessage(String to) throws Exception {

        ePw = createKey();


        MimeMessage message = createMessage(to);
        try {
            emailsender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException("이메일 발송 중 오류가 발생했습니다.");
        }


        return ePw;
    }
}