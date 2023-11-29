package com.strcat.controller;


import com.strcat.dto.ReadMyInfoResDto;
import com.strcat.service.BoardGroupService;
import com.strcat.service.BoardService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    private final BoardGroupService boardGroupService;

    @GetMapping("/boards")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<ReadMyInfoResDto> readMyBoardInfo(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return boardService.readMyBoardInfo(token);
    }

    @GetMapping("/board-groups")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<ReadMyInfoResDto> readMyBoardGroupInfo(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return boardGroupService.readMyBoardGroupInfo(token);
    }
}
