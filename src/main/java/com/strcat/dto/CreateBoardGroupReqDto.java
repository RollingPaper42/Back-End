package com.strcat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CreateBoardGroupReqDto {
    private final String title;

    @JsonCreator
    public CreateBoardGroupReqDto(@JsonProperty("title") String title) {
        this.title = title;
    }
}
