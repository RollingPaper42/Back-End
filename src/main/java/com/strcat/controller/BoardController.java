package com.strcat.controller;

import com.strcat.domain.Board;
import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.CreateContentReqDto;
import com.strcat.dto.ReadBoardInfoResDto;
import com.strcat.service.BoardService;
import com.strcat.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final ContentService contentService;
    private final BoardService boardService;

    @PostMapping("/{boardId}/contents")
    public void createContent(@PathVariable(name = "boardId") String encryptedBoardId,
                              @RequestBody CreateContentReqDto dto) throws Exception {
        contentService.create(dto, encryptedBoardId);
    }

    @PostMapping
    public String createBoard(@RequestHeader("Authorization") String token,
                              @RequestBody CreateBoardReqDto dto) throws Exception{
        return boardService.createBoard(dto, token);
    }

    // 삭제 예정
    @GetMapping("/{boardId}")
    public ReadBoardInfoResDto readBoardInfo(@PathVariable(name = "boardId") String encryptedBoardId) throws Exception {
        return boardService.readBoardInfo(encryptedBoardId);
    }

    @GetMapping("/{boardId}/contents")
    public Board readBoard(@PathVariable(name = "boardId") String encryptedBoardId) throws Exception {
        return boardService.readBoard(encryptedBoardId);
    }
}
