package com.strcat.controller;

import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.CreateContentReqDto;
import com.strcat.dto.ReadBoardResDto;
import com.strcat.dto.ReadBoardSummaryResDto;
import com.strcat.service.BoardService;
import com.strcat.service.ContentService;
import com.strcat.service.PictureService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "보드 및 컨텐츠")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "406", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러"),
})
@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final ContentService contentService;
    private final BoardService boardService;
    private final PictureService pictureService;


    @PostMapping("/{boardId}/contents")
    public Long createContent(@PathVariable(name = "boardId") String encryptedBoardId,
                              @RequestBody CreateContentReqDto dto) {
        return contentService.create(dto, encryptedBoardId).getId();
    }

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public String createBoard(@Parameter(hidden = true) @RequestHeader("Authorization") String token,
                              @RequestBody CreateBoardReqDto dto) {
        return boardService.createBoard(dto, token);
    }

    @GetMapping("/{boardId}/contents")
    public ReadBoardResDto readBoard(@RequestHeader("Authorization") String token,
                                     @PathVariable(name = "boardId") String encryptedBoardId) {
        return boardService.readBoard(encryptedBoardId, token);
    }

    @GetMapping("/{boardId}/summaries")
    @SecurityRequirement(name = "Bearer Authentication")
    public ReadBoardSummaryResDto readSummary(@PathVariable(name = "boardId") String encryptedBoardId,
                                              @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return boardService.readSummary(encryptedBoardId, token);
    }

    @PostMapping("/{boardId}/contents/pictures")
    public String createPicture(@PathVariable(name = "boardId") String encryptedBoardId,
                                @RequestParam MultipartFile picture) {
        return pictureService.postPicture(encryptedBoardId, picture);
    }
}
