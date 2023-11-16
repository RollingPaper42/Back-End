package com.strcat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContentCreateDto {
    private final String writer;

    private final String text;

    private final Long boardId;
}
