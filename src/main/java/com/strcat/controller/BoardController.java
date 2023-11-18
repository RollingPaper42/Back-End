package com.strcat.controller;

import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.CreateContentReqDto;
import com.strcat.service.BoardService;
import com.strcat.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller("/boards")
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
                              @RequestBody CreateBoardReqDto dto) {
        // jwt 까서 userId 찾은 후 createBoard() 에 넘기ㅁ
        boardService.createBoard(dto, token);
        return "link";
    }
}
