package com.strcat.dto;

import com.strcat.domain.Content;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BoardResponse {
    private final String id;
    private final String title;
    private final String theme;
    private final List<Content> contents;
}
