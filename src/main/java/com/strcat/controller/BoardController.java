package com.strcat.controller;

import com.strcat.dto.CreateContentReqDto;
import com.strcat.exception.NotAcceptableException;
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

    @PostMapping("/{boardId}/content")
    public void createContent(@PathVariable(name = "boardId") String encryptedBoardId,
                              @RequestBody CreateContentReqDto dto) {
        try {
            contentService.create(dto, encryptedBoardId);
        } catch (Exception e) {
            throw new NotAcceptableException();
        }
    }
}
