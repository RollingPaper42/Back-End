package com.strcat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReadBoardGroupSummaryResDto {
    private final String title;
    private final Long contentCount;
    private final Integer boardCount;
    private final Long contentTextCount;
}
