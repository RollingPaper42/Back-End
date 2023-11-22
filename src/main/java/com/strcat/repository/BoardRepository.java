package com.strcat.repository;

import com.strcat.domain.Board;
import com.strcat.dto.ReadMyBoardInfoResDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<ReadMyBoardInfoResDto> findByUserId(Long userId);
}
