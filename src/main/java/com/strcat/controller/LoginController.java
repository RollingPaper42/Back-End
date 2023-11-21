package com.strcat.controller;

import com.strcat.dto.CheckLoginResDto;
import com.strcat.service.UserService;
import com.strcat.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;

    @GetMapping("/check")
    public CheckLoginResDto checkLogin(@RequestHeader("Authorization") String token) {
        return new CheckLoginResDto(userService.isLogin(token));
    }
}
