package com.strcat.repository;

import com.strcat.domain.BoardGroup;
import com.strcat.dto.ReadBoardGroupBoardInfoResDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardGroupRepository extends JpaRepository<BoardGroup, Long> {
    @Query(value = """
            SELECT new com.strcat.dto.ReadBoardGroupBoardInfoResDto(board.id, board.title, board.theme)
            FROM Board board
            LEFT JOIN BoardGroup boardGroup ON board.boardGroup.id = boardGroup.id
            WHERE boardGroup.id = (:boardGroupId)
            """)
    List<ReadBoardGroupBoardInfoResDto> findBoardInfo(@Param("boardGroupId") Long boardGroupId);
}
