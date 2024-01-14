package com.strcat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strcat.dto.BoardResponse;
import com.strcat.dto.ReadBoardResDto;
import com.strcat.dto.ReadBoardSummaryResDto;
import com.strcat.dto.ReadMyInfoResDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@ToString
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String title;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "theme", nullable = false)
    private String theme;

    @Column(name = "encrypted_id", columnDefinition = "TEXT", unique = true)
    private String encryptedId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // 외래키 컬럼 지정
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Content> contents = new ArrayList<>();

    public Board(String title, String theme, User user) {
        this.title = title;
        this.theme = theme;
        this.user = user;
    }

    public Long calculateTotalContentLength() {
        return contents.stream()
                .mapToLong(content -> content.getText().length())
                .sum();
    }

    private BoardResponse toBoardResponse() {
        return new BoardResponse(encryptedId, title, theme, contents);
    }

    public ReadBoardResDto toReadBoardResDto(Boolean isOwner) {
        return new ReadBoardResDto(isOwner, toBoardResponse());
    }

    public ReadBoardSummaryResDto toReadBoardSummaryDto() {
        return new ReadBoardSummaryResDto(title, theme, contents.size(), calculateTotalContentLength());
    }

    public ReadMyInfoResDto toReadMyInfoResDto() {
        return new ReadMyInfoResDto(encryptedId, title);
    }
}
