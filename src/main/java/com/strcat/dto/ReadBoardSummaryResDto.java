package com.strcat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReadBoardSummaryResDto {
    private final String title;
    private final Integer contentCount;
    private final Long contentTextCount;
}
