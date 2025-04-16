package com.example.project.service;

import com.example.project.entity.SearchHistory;
import com.example.project.entity.User;
import com.example.project.repository.SearchHistoryRepository;
import com.example.project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SearchLogService {
    private final UserRepository userRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    public SearchLogService(UserRepository userRepository, SearchHistoryRepository searchHistoryRepository) {
        this.userRepository = userRepository;
        this.searchHistoryRepository = searchHistoryRepository;
    }

    public void saveSearchLog(String email, String query) {
        User user = userRepository.findByEmail(email);
        System.out.println("검색 로그 저장 대상: " + user);

        if (user == null) return;

        SearchHistory history = SearchHistory.builder()
                .songName(query)
                .searchedAt(LocalDateTime.now())
                .userId(user.getId())
                .build();

        searchHistoryRepository.create(history);
    }
}

