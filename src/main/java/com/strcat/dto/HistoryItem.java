package com.strcat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(example = """
            {
              "encryptedBoardId": "string",
              "title": "string",
              "visitTime": "2024-02-07 07:54:54"
            }
        """)
public record HistoryItem(String encryptedBoardId, String title,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
                          LocalDateTime visitTime) {
}
