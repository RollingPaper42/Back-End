package com.strcat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateBoardReqDto {
    private final String title;
    private final String theme;
    @JsonProperty("public")
    private final Boolean isPublic;
}
