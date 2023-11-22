package com.strcat.controller;

import com.strcat.dto.ReadMyBoardInfoResDto;
import com.strcat.service.BoardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final BoardService boardService;
    @GetMapping("/boards")
    public List<ReadMyBoardInfoResDto> readMyBoardInfo(@RequestHeader("Authorization") String token) {
        return boardService.readMyBoardInfo(token);
    }
}