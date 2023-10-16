package com.example.demo.config.auth;

import com.example.demo.config.auth.provider.KakaoUserInfo;
import com.example.demo.config.auth.provider.NaverUserInfo;
import com.example.demo.config.auth.provider.OAuth2UserInfo;
import com.example.demo.domain.dto.UserDto;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class PrincipalDetailsOAuth2Service extends DefaultOAuth2UserService implements UserDetailsService {

    private PasswordEncoder passwordEncoder;

    public PrincipalDetailsOAuth2Service() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oauth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = null;
        System.out.println("auth2User.getAttributes() : " + oauth2User.getAttributes());
        if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            System.out.println("카카오 로그인");
            KakaoUserInfo kakaoUserInfo = new KakaoUserInfo((Map<String, Object>) oauth2User.getAttributes().get("properties"));
            kakaoUserInfo.setId(userRequest.getClientRegistration().getClientId());
            oAuth2UserInfo = kakaoUserInfo;
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("네이버 로그인");
            oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oauth2User.getAttributes().get("response"));
        }
        //OAuth2UserInfo확인
        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String password = passwordEncoder.encode("1234");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_MEMBER";
        //DB저장
        Optional<User> optional = userRepository.findById(email);
        if (optional.isEmpty()) {
            User user = User.builder()
                    .email(email)
                    .password(password)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(user);
            System.out.println("[OAUTH] " + provider + " 최초 로그인 요청!");
        } else {
            System.out.println("[OAUTH] 기존 계정 " + optional.get().getEmail() + "으로 로그인!");

        }
        //AccessToken 정보를 Authenication에 저장하기
        PrincipalDetails principalDetails = new PrincipalDetails();
        principalDetails.setAttributes(oauth2User.getAttributes());
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setRole(role);
        //OAUTH2 LOGOUT
        dto.setProvider(provider);
        dto.setProviderId(providerId);
        principalDetails.setUser(dto);
        principalDetails.setAccessToken(userRequest.getAccessToken().getTokenValue());

        return principalDetails;

    }

    @Override
    public UserDetails loadUserByUsername(String Email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(Email);

        if (user.isEmpty())
            return null;
        UserDto dto = new UserDto();
        dto.setEmail(user.get().getEmail());
        dto.setPassword(user.get().getPassword());
        dto.setRole(user.get().getRole());
        dto.setPhone(user.get().getPhone());
        dto.setName(user.get().getName());
        dto.setNickname(user.get().getNickname());
        dto.setZipcode(user.get().getZipcode());
        dto.setAddr1(user.get().getAddr1());
        dto.setAddr2(user.get().getAddr2());
        dto.setBirth(user.get().getBirth());
        dto.setQuestion(user.get().getQuestion());
        dto.setAnswer(user.get().getAnswer());

        PrincipalDetails principalDetails = new PrincipalDetails();
        principalDetails.setUser(dto);

        return principalDetails;

    }
}

