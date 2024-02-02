package com.strcat.dto;

import java.util.List;

public record DeleteContentReqDto(
        List<Long> contentIds
) {
}
