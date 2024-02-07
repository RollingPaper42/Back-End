package com.strcat.repository;

import com.strcat.domain.History;
import com.strcat.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface HistoryRepository extends JpaRepository<History, Long> {
    public List<History> findByUserIdOrderByVisitedAtAsc(Long userId);

    @Modifying
    @Query("DELETE FROM History h WHERE h.user = :user")
    public void deleteHistoriesByUser(User user);

    @Modifying
    @Query("DELETE FROM History h WHERE h.id = :id")
    public void deleteById(Long id);

}
