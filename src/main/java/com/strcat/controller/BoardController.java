package com.strcat.controller;

import com.strcat.dto.CreateContentReqDto;
import com.strcat.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller("/board")
@RequiredArgsConstructor
public class BoardController {
    private final ContentService contentService;
//    private final BoardService boardService;

    @PostMapping("/{boardId}/content")
    public void createContent(@PathVariable(name = "boardId") String encryptedBoardId,
                              @RequestBody CreateContentReqDto dto) throws Exception {
        contentService.create(dto, encryptedBoardId);
    }
}
