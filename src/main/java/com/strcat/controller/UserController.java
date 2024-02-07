package com.strcat.controller;


import com.strcat.dto.HistoryDto;
import com.strcat.dto.ReadMyInfoResDto;
import com.strcat.service.BoardService;
import com.strcat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = {
                @Content(examples = {@ExampleObject("인증 실패")})
        }),
        @ApiResponse(responseCode = "406", description = "잘못된 요청", content = {
                @Content(examples = {@ExampleObject("잘못된 요청")})
        }),
        @ApiResponse(responseCode = "500", description = "서버 에러", content = {
                @Content(examples = {@ExampleObject("서버 에러")})
        }),
})
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final BoardService boardService;
    private final UserService userService;

    @GetMapping("/boards")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "내 보드 조회", description = "내가 생성한 보드 정보 리스트를 반환합니다.")
    public List<ReadMyInfoResDto> readMyBoardInfo(
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        return userService.readMyBoardInfo(userId);
    }
    @GetMapping("/history")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "최근 방문한 보드 조회", description = "내가 최근에 방문한 보드 리스트를 반환합니다.")
    public HistoryDto readMyBoardHistory(
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return userService.readMyBoardHistory(userId);
    }

    @PostMapping("/history")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "최근 방문한 보드 저장", description = "내가 최근에 방문한 보드 리스트를 저장합니다.")
    public HistoryDto postMyBoardHistory(
            Authentication authentication, @RequestBody HistoryDto dto) {
        Long userId = (Long) authentication.getPrincipal();

        return userService.postMyBoardHistory(userId, dto);
    }
}
