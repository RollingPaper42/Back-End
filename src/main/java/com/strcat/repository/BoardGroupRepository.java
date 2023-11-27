package com.strcat.repository;

import com.strcat.domain.BoardGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardGroupRepository extends JpaRepository<BoardGroup, Long> {
//    List<ReadMyBoardGroupInfoResDto> findByUserId(Long id);

    //테스트용
    List<BoardGroup> findByUserId(Long id);
}
