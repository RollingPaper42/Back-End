package com.strcat.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class ReadBoardResDto {
    private final Boolean isOwner;
    private final BoardResponse board;
}
