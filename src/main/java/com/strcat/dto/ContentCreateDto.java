package com.strcat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContentCreateDto {
    private final String writer;

    private final String text;

    private final Long boardId;
}
