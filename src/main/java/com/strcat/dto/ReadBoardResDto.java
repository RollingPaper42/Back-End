package com.strcat.dto;

import com.strcat.domain.Board;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReadBoardResDto {
    private final Boolean isOwner;
    private final Board board;
}
