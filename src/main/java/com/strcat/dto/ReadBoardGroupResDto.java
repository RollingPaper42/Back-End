package com.strcat.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReadBoardGroupResDto {
    private final String title;
    private final List<ReadBoardGroupBoardInfoResDto> boardInfo;
}
