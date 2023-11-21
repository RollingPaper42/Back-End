package com.strcat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    @GetMapping("/boards")
    public void readMyBoardInfo(@RequestHeader("Authorization") String token) {
        // TODO: 내가 만든 보드 정보 가져오기

    }
}
