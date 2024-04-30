package com.strcat.repository;

import com.strcat.domain.Board;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByEncryptedId(String encryptedId);
    List<Board> findByIsPublicTrue();
}
