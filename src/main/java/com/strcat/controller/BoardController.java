package com.strcat.controller;

import com.strcat.domain.Board;
import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.CreateContentReqDto;
import com.strcat.dto.ReadBoardSummaryResDto;
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
                              @RequestBody CreateContentReqDto dto) {
        contentService.create(dto, encryptedBoardId);
    }

    @PostMapping
    public String createBoard(@RequestHeader("Authorization") String token,
                              @RequestBody CreateBoardReqDto dto) throws Exception {
        return boardService.createBoard(dto, token);
    }

    @GetMapping("/{boardId}/contents")
    public Board readBoard(@PathVariable(name = "boardId") String encryptedBoardId) {
        return boardService.readBoard(encryptedBoardId);
    }

    @GetMapping("/{boardId}/summaries")
    public ReadBoardSummaryResDto readSummary(@PathVariable(name = "boardId") String encryptedBoardId,
                                              @RequestHeader("Authorization") String token) throws Exception {
        return boardService.readSummary(encryptedBoardId, token);
    }
}
