package com.strcat.controller;

import com.strcat.dto.CheckLoginResDto;
import com.strcat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그인")
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;

    @GetMapping("/check")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "로그인 여부 확인", description = "현재 로그인 여부를 반환합니다.")
    public CheckLoginResDto checkLogin(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ) {
        return new CheckLoginResDto(userService.isLogin(token));
    }
}
