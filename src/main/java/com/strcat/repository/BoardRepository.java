package com.strcat.repository;

import com.strcat.domain.Board;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByBoardGroupId(Long boardGroupId);
}
