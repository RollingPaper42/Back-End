package com.strcat.controller;

import com.strcat.dto.CreateBoardGroupReqDto;
import com.strcat.dto.ReadBoardGroupResDto;
import com.strcat.dto.ReadBoardGroupSummaryResDto;
import com.strcat.service.BoardGroupService;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "보드 그룹")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "406", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러"),
})
@RestController
@RequestMapping("/board-groups")
@RequiredArgsConstructor
public class BoardGroupController {
    private final BoardGroupService boardGroupService;

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public String createGroup(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @RequestBody CreateBoardGroupReqDto dto) {
        return boardGroupService.create(dto, token);
    }

    @GetMapping("/{boardGroupId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ReadBoardGroupResDto readBoardGroup(@Parameter(hidden = true) @RequestHeader("Authorization") String token,
                                               @PathVariable(name = "boardGroupId") String encryptedBoardGroupId) {
        return boardGroupService.readBoardGroup(encryptedBoardGroupId, token);
    }

    @GetMapping("/{boardGroupId}/summaries")
    @SecurityRequirement(name = "Bearer Authentication")
    public ReadBoardGroupSummaryResDto readBoardGroupSummary(
            @PathVariable(name = "boardGroupId") String encryptedBoardGroupId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return boardGroupService.readSummary(encryptedBoardGroupId, token);
    }
}
