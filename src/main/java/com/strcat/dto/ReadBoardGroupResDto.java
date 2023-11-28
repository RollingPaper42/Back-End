package com.strcat.dto;

import com.strcat.domain.Board;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class ReadBoardGroupResDto {
    private final String title;
    private final String encryptedId;
    private final Boolean isOwner;
    private final List<Board> boards;
}
