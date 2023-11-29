package com.strcat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReadMyBoardInfoResDto {
    private final String id;
    private final String title;
}
