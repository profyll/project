package com.example.project.repository;

import com.example.project.entity.Artist;
import com.example.project.entity.SearchHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SearchHistoryRepository {
    public int create(SearchHistory searchHistory);
    public List<SearchHistory> findByUserId(@Param("userId") int userId);
}
