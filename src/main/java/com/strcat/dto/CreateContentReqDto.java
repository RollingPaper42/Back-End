package com.strcat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateContentReqDto {
    private final String writer;
    private final String text;
    private final String photoUrl;
}
