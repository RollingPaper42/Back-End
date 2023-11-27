package com.strcat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReadMyBoardInfoResDto {
    private final String encryptedBoardId;
    private final String title;
    private final String theme;
}
