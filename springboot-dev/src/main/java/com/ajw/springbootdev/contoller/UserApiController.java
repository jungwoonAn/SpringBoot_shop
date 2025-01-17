package com.ajw.springbootdev.contoller;

import com.ajw.springbootdev.dto.AddUserRequest;
import com.ajw.springbootdev.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserApiController {
    
    private final UserService userService;

    // 회원 가입
    @PostMapping("/user")
    public String signup(AddUserRequest request){
        userService.save(request);  // 회원 가입 메서드 호출
        return "redirect:/login";  // 회원 가입이 완료된 이후에 로그인 페이지로 이동
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) { // 인증된 사용자가 있을 경우
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/login"; // 로그아웃 후 리다이렉트
    }
}
