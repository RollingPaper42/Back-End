package com.strcat.controller;

import com.strcat.dto.CreateBoardGroupReqDto;
import com.strcat.dto.ReadBoardGroupResDto;
import com.strcat.dto.ReadBoardGroupSummaryResDto;
import com.strcat.service.BoardGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board-groups")
@RequiredArgsConstructor
public class BoardGroupController {
    private final BoardGroupService boardGroupService;

    @PostMapping
    public String createGroup(@RequestHeader("Authorization") String token,
                              @RequestBody CreateBoardGroupReqDto dto) {
        return boardGroupService.create(dto, token);
    }

    @GetMapping("/{boardGroupId}/boards")
    public ReadBoardGroupResDto readBoardGroup(@PathVariable(name = "boardGroupId") String encryptedBoardGroupId) {
        return boardGroupService.readBoardGroup(encryptedBoardGroupId);
    }

    @GetMapping("/{boardGroupId}/summaries")
    public ReadBoardGroupSummaryResDto readBoardGroupSummary(
            @PathVariable(name = "boardGroupId") String encryptedBoardGroupId,
            @RequestHeader("Authorization") String token) {
        return boardGroupService.readSummary(encryptedBoardGroupId, token);
    }

}
