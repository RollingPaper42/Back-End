package com.strcat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.strcat.domain.Content;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class BoardResponse {
    private final String id;
    private final String title;
    private final String theme;
    @JsonProperty("public")
    private final Boolean isPublic;
    private final List<Content> contents;
}
