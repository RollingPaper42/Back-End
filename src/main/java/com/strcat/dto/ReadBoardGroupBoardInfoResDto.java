package com.strcat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReadBoardGroupBoardInfoResDto {
    private final Long id;
    private final String title;
    private final String theme;
}
