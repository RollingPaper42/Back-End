package com.strcat.dto;

import com.strcat.domain.Board;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReadBoardGroupResDto {
    private final String title;
    private final Boolean isOwner;
    private final List<Board> boards;
}
