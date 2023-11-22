package com.strcat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public class CreateBoardGroupReqDto {
    private final String title;

    @JsonCreator
    public CreateBoardGroupReqDto(String title) {
        this.title = title;
    }
}
