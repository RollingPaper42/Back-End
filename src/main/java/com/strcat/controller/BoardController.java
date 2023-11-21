package com.strcat.controller;

import com.strcat.domain.Board;
import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.CreateContentReqDto;
import com.strcat.dto.ReadBoardInfoResDto;
import com.strcat.dto.ReadBoardSummaryResDto;
import com.strcat.service.BoardService;
import com.strcat.service.ContentService;
import com.strcat.service.PictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final ContentService contentService;
    private final BoardService boardService;
    private final PictureService pictureService;

    @PostMapping("/{boardId}/contents")
    public void createContent(@PathVariable(name = "boardId") String encryptedBoardId,
                              @RequestBody CreateContentReqDto dto) {
        contentService.create(dto, encryptedBoardId);
    }

    @PostMapping
    public String createBoard(@RequestHeader("Authorization") String token,
                              @RequestBody CreateBoardReqDto dto) {
        return boardService.createBoard(dto, token);
    }

    // 삭제 예정
    @GetMapping("/{boardId}")
    public ReadBoardInfoResDto readBoardInfo(@PathVariable(name = "boardId") String encryptedBoardId) {
        return boardService.readBoardInfo(encryptedBoardId);
    }

    @GetMapping("/{boardId}/contents")
    public Board readBoard(@PathVariable(name = "boardId") String encryptedBoardId) {
        return boardService.readBoard(encryptedBoardId);
    }

    @GetMapping("/{boardId}/summaries")
    public ReadBoardSummaryResDto readSummary(@PathVariable(name = "boardId") String encryptedBoardId,
                                              @RequestHeader("Authorization") String token) {
        return boardService.readSummary(encryptedBoardId, token);
    }
//    http://localhost:8080/boards/Vvs_JTGorbxqVWXr6aH0cg==/contents/1/pictures
    @PostMapping("/{boardId}/contents/{contentId}/pictures")
    public String createPicture(@PathVariable(name = "boardId") String encryptedBoardId,
                                @PathVariable String contentId,
                                @RequestParam MultipartFile picture) {
        return pictureService.postPicture(encryptedBoardId, contentId, picture);
    }
}
