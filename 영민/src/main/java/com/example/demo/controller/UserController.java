package com.example.demo.controller;

import com.example.demo.domain.dto.UserDto;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.BoardRepository;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.domain.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Controller
@Slf4j
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BoardRepository boardRepository;


    @PostMapping("/login")
    public String login_post(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session, Model model) {
        log.info("GET/login");


        return "redirect:/list";
    }
//================================================================
@PostMapping("/checkDuplicate")
public ResponseEntity<Map<String, Boolean>> checkDuplicate(@RequestBody Map<String, String> requestBody) {
    String field = requestBody.get("field");
    String value = requestBody.get("value");

    boolean isDuplicate = false;

    if ("emailInput".equals(field)) {
        isDuplicate = userRepository.existsByEmail(value);
    }
    Map<String, Boolean> response = new HashMap<>();
    response.put("duplicate", isDuplicate);

    return ResponseEntity.ok(response);
}
    //================================================================

    @GetMapping("/join")
    public void join_get(){
        log.info("GET/join");
        }
    @PostMapping("/join")
        public String join_post(@Valid UserDto dto, BindingResult bindingResult, Model model, HttpServletRequest request, HttpServletResponse response){
            log.info("GET/join"+dto);

        dto.setRole("ROLE_MEMBER");
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        User user = new User();

        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setNickname(dto.getNickname());
        user.setName(dto.getName());
        user.setZipcode(dto.getZipcode());
        user.setAddr1(dto.getAddr1());
        user.setAddr2(dto.getAddr2());
        user.setBirth(dto.getBirth());
        user.setPhone(dto.getPhone());
        user.setQuestion(dto.getQuestion());
        user.setAnswer(dto.getAnswer());

        user.setProvider(dto.getProvider());
        user.setProviderId(dto.getProviderId());
        user.setRole(dto.getRole());

        userRepository.save(user);



        boolean isjoin = userService.joinMember(dto,model,request);
            if(!isjoin){
                return "/join";
            }
            //04
        return "redirect:/login?msg=Join_Success!";
    }

    @GetMapping("/profile/update")
    public String showInfo(Model model) {
        // 현재 인증된 사용자의 이메일 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // UserDto 객체 생성
        UserDto dto = new UserDto();

        // UserRepository를 사용하여 사용자 정보 가져오기
        Optional<User> user = userRepository.findByEmail(email);

        // 사용자 정보에서 닉네임을 가져와서 설정
        if (user.isPresent()) {
            dto.setNickname(dto.getNickname());
            dto.setPassword(dto.getPassword());
            dto.setBirth(dto.getBirth());
            dto.setPhone(dto.getPhone());
            dto.setZipcode(dto.getZipcode());
            dto.setAddr1(dto.getAddr1());
            dto.setAddr2(dto.getAddr2());
            dto.setQuestion(dto.getQuestion());
            dto.setAnswer(dto.getAnswer());
            dto.setName(dto.getName());
        }

        model.addAttribute("dto", dto);

        return "profile/update";
    }







}
