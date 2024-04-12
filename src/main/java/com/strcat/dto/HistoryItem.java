package com.strcat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(example = """
            {
              "encryptedBoardId": "string",
              "title": "string",
              "visitTime": "2024-02-07 07:54:54"
            }
        """)
public record HistoryItem(String encryptedBoardId, String title, @JsonProperty("public") Boolean isPublic,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
                          LocalDateTime visitTime) {
}
