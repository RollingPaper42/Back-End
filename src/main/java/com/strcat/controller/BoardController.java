package com.strcat.controller;

import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.CreateContentReqDto;
import com.strcat.dto.ReadBoardResDto;
import com.strcat.dto.ReadBoardSummaryResDto;
import com.strcat.service.BoardService;
import com.strcat.service.ContentService;
import com.strcat.service.PictureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
        @ApiResponse(responseCode = "406", description = "잘못된 요청", content = {
                @Content(examples = {@ExampleObject("잘못된 요청")})
        }),
        @ApiResponse(responseCode = "500", description = "서버 에러", content = {
                @Content(examples = {@ExampleObject("서버 에러")})
        }),
})
@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final ContentService contentService;
    private final BoardService boardService;
    private final PictureService pictureService;

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "보드 생성", description = "생성 성공 후 board의 encryptedId를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공", content = {
            @Content(examples = {@ExampleObject("Wd5lUSQnmEjnMVl043cEzZzNqqrA3Z9pBAVImYNwI14=")})})
    @ApiResponse(responseCode = "401", description = "인증 실패", content = {
            @Content(examples = {@ExampleObject("인증 실패")})
    })
    public String createBoard(@Parameter(hidden = true) @RequestHeader("Authorization") String token,
                              @RequestBody CreateBoardReqDto dto) {
        return boardService.createBoard(dto, token);
    }

    @PostMapping("/{boardId}/contents")
    @Operation(summary = "컨텐츠 생성", description = "컨텐츠 성공 후 content의 id를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공", content = {
            @Content(examples = {@ExampleObject("32")})})
    public Long createContent(@PathVariable(name = "boardId") String encryptedBoardId,
                              @RequestBody CreateContentReqDto dto) {
        return contentService.create(dto, encryptedBoardId).getId();
    }

    @PostMapping("/{boardId}/contents/pictures")
    @Operation(summary = "사진 업로드", description = "사진 저장 성공 후 사진url을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공", content = {
            @Content(examples = {
                    @ExampleObject("https://elasticbeanstalk-ap-northeast-2-168479654979.s3.ap-northeast-2.amazonaws.com/pictures/strcat%3Aj5AAnW0Dq0Q5qS4g56IiRj1W8xQUKPbva1hbe_4STryyELo6vaiFqPgjJH5-_iIv%3A1701366068275%3Aphotofilename.jpg")})})
    public String createPicture(@PathVariable(name = "boardId") String encryptedBoardId,
                                @RequestParam MultipartFile picture) {
        return pictureService.postPicture(encryptedBoardId, picture);
    }

    @GetMapping("/{boardId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "보드 조회", description = "보드에 대한 모든 정보와 보드 소유자 여부를 반환합니다.")
    public ReadBoardResDto readBoard(@Parameter(hidden = true) @RequestHeader("Authorization") String token,
                                     @PathVariable(name = "boardId") String encryptedBoardId) {
        return boardService.readBoard(encryptedBoardId, token);
    }

    @GetMapping("/{boardId}/summaries")
    @Operation(summary = "보드 요약 조회", description = "보드 요약 정보를 조회합니다.")
    public ReadBoardSummaryResDto readSummary(@PathVariable(name = "boardId") String encryptedBoardId) {
        return boardService.readSummary(encryptedBoardId);
    }
}
