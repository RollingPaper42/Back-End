package com.strcat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReadMyInfoResDto {
    private final String id;
    private final String title;
    @JsonProperty("public")
    private final Boolean isPublic;
}
