package com.strcat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strcat.dto.HistoryDto;
import com.strcat.dto.HistoryItem;
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
import java.time.ZoneOffset;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@ToString(exclude = {"user", "board"})
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;


    public History(User user, Board board, LocalDateTime visitedAt) {
        this.user = user;
        this.board = board;
        this.visitedAt = visitedAt;
    }

    public HistoryItem toDto() {
        return new HistoryItem(board.getEncryptedId(), board.getTitle(), visitedAt);
    }
}
