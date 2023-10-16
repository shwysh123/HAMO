package com.example.demo.domain.dto;

import com.example.demo.domain.entity.User;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserDto {

    @NotBlank(message = "ID를 입력하세요.")
    @Email(message = "올바른 이메일 주소를 입력하세요")
    private String email;  //Email 형식
    @NotBlank(message = "password를 입력하세요")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;
    @NotBlank(message = "password를 다시 입력하세요")
//    private String repassword;
//    @AssertTrue(message = "비밀번호가 같지 않습니다")
//    private boolean isValid(){
//        return password.equals(repassword);
//    }         html로 옮겨야할 사항
    private String nickname;
    private String name;
    private String phone;
    private String birth;
    private String addr1;
    private String addr2;
    private String zipcode;
    private String role;
    private String question;    // id, pw 찾을때 찾는방법 회원가입할때 여러질문 중 고르기
    private String answer;  // id , pw 찾을때 찾는방법 회원가입할때 여러질문 중 고른 질문에 대한 답

    //OAuth2
    private String provider;
    private String providerId;

    private String profile;
    public static User dtoToEntity(User dto){
        User user = User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(dto.getRole())
                .nickname(dto.getNickname())
                .name(dto.getName())
                .phone(dto.getPhone())
                .birth(dto.getBirth())
                .zipcode(dto.getZipcode())
                .addr1(dto.getAddr1())
                .addr2(dto.getAddr2())
                .question(dto.getQuestion())
                .answer(dto.getAnswer())
                .build();
        return user;
    }
}
