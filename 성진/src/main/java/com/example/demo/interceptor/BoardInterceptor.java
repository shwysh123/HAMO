package com.example.demo.interceptor;

import com.example.demo.config.auth.PrincipalDetails;
import com.example.demo.domain.entity.Board;
import com.example.demo.domain.repository.BoardRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BoardInterceptor implements HandlerInterceptor {

    private final BoardRepository boardRepository;

    public BoardInterceptor(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        //게시물작성 사용자 정보
        long number = Long.parseLong(request.getParameter("number"));
        Board board = boardRepository.findById(number).get();
        System.out.println("[Interceptor] Board Update Interceptor..."+board);
        String boardNickname = board.getNickname();
        //접속중인 사용자 정보

        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String authUsername = principalDetails.getUser().getNickname();

        if(!boardNickname.equals(authUsername)){
            throw new Exception("권한이 없습니다");
        }
        return true; //계정정보 일치시 Update 페이지로 진입
    }
    @Override
    public void postHandle(HttpServletRequest request,HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception{
        HandlerInterceptor.super.postHandle(request, response , handler, modelAndView);
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception{
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
