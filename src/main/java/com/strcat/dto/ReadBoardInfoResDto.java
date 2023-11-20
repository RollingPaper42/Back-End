package com.strcat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReadBoardInfoResDto {
    private final String title;
    private final String backgroundColor;
}
