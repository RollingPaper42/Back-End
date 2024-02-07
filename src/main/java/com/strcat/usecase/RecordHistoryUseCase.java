package com.strcat.usecase;

import com.strcat.domain.Board;
import com.strcat.domain.History;
import com.strcat.domain.User;
import com.strcat.dto.HistoryItem;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.HistoryRepository;
import com.strcat.repository.UserRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecordHistoryUseCase {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final HistoryRepository historyRepository;

    public List<History> record(Long userId, List<HistoryItem> historyItems) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotAcceptableException("유저를 찾을 수 없습니다"));

        Queue<History> histories = new LinkedList<>(historyItems.stream().map((item) -> {
            Board board = boardRepository.findByEncryptedId(item.encryptedBoardId())
                    .orElseThrow(() -> new NotAcceptableException("보드를 찾을 수 없습니다"));

            return new History(user, board, item.visitTime());
        }).sorted(Comparator.comparing(History::getVisitedAt)).toList());

        List<History> origins = user.getHistories();
        
        renewHistories(histories, origins);
        filterLeastHistory(origins);

        return historyRepository.findByUserIdOrderByVisitedAtAsc(userId);
    }

    // 모든 기록이 담긴 PriorityQueue에서 최신 10개의 데이터만 남기고 삭제하고 DB에 적용
    private void filterLeastHistory(List<History> origins) {
        Queue<History> priorityQ = new PriorityQueue<>(Comparator.comparing(History::getVisitedAt).reversed());

        priorityQ.addAll(origins);

        while (priorityQ.size() > 10) {
            History history = priorityQ.poll();
            historyRepository.deleteById(history.getId());
        }

        historyRepository.saveAll(priorityQ);
    }

    // 이미 해당 보드의 기록이 존재하면 보다 최신 기록으로 갱신하고 아니라면 새 기록을 추가함
    private void renewHistories(Queue<History> histories, List<History> origins) {

        while (!histories.isEmpty()) {
            History history = histories.peek();
            boolean isIn = false;

            for (History origin: origins) {
                if (origin.getBoard().getEncryptedId().equals(history.getBoard().getEncryptedId())) {
                    if (origin.getVisitedAt().compareTo(history.getVisitedAt()) > 0) {
                        origin.setVisitedAt(history.getVisitedAt());
                    }
                    isIn = true;
                    break;
                }
            }
            if (!isIn) {
                origins.add(history);
            }
            histories.poll();
        }
    }
}
