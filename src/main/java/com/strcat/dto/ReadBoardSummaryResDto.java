package com.strcat.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ReadBoardSummaryResDto {
    private final String title;
    private final Integer contentCount;
    private final Long contentTextCount;
}
