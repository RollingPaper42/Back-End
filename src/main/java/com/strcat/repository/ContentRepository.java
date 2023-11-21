package com.strcat.repository;

import com.strcat.domain.Content;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    Optional<Content> findByBoardId(Long aLong);
    @Query("SELECT COUNT(c) FROM Content c WHERE c.photoUrl IS NOT NULL")
    long countExistPhoto();
}
