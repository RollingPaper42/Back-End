package com.strcat.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class ReadBoardGroupSummaryResDto {
    private final String title;
    private final Long contentCount;
    private final Integer boardCount;
    private final Long contentTextCount;
}
