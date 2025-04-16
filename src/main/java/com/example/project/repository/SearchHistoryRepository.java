package com.example.project.repository;

import com.example.project.entity.Artist;
import com.example.project.entity.SearchHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SearchHistoryRepository {
    public int create(SearchHistory searchHistory);
}
