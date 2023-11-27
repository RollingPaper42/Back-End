package com.strcat.controller;

import com.strcat.dto.TmpReadMyBoardGroupInfoResDto;
import com.strcat.service.BoardGroupService;
import io.swagger.v3.oas.annotations.Parameter;
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
    private final BoardGroupService boardGroupService;

    @GetMapping("/board-groups")
    public List<TmpReadMyBoardGroupInfoResDto> readMyBoardGroupInfo(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return boardGroupService.readMyBoardGroupInfo(token);
    }
}
