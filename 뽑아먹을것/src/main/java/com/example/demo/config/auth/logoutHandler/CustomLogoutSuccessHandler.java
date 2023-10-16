package com.example.demo.config.auth.logoutHandler;

import com.example.demo.config.auth.PrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.thymeleaf.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private String kakaoClientId = "89cad9eb7ce326d98a5087eb0def013a";
    private String LOGOUT_REDIRECT_URI = "http://localhost:8080/login";

    private String naverClientId = "xjaJBUp_t9e_Lul6ZzsO";
    private String naverClientSecret = "ul2LQDOTO5";

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("CustomLogoutSuccessHandler's onLogoutSuccess!");

        PrincipalDetails principalDetails = (PrincipalDetails)  authentication.getPrincipal();
        String provider = principalDetails.getUser().getProvider();
        if(StringUtils.contains(provider,"kakao")){
            System.out.println("GET/th/kakao/logoutWithKakao");
            //URL
            String url = "https://kauth.kakao.com/oauth/logout?client_id="+kakaoClientId+"&logout_redirect_uri="+LOGOUT_REDIRECT_URI;
            response.sendRedirect(url);
            return;
        }
        else if (StringUtils.contains(provider,"naver")){
            String url = "http://nid.naver.com/nidlogin.logout";
            response.sendRedirect(url);
            return;
        }
        response.sendRedirect("/login");
    }
}
