package com.strcat.dto;

import com.strcat.domain.Content;
import java.util.List;

public record DeleteContentResDto(List<Content> contents) {
}
