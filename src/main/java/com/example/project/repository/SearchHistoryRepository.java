package com.example.project.repository;

import com.example.project.entity.Artist;
import com.example.project.entity.SearchHistory;
import com.example.project.vo.SearchHistoryWithSong;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SearchHistoryRepository {
    public int create(SearchHistory searchHistory);
    List<SearchHistory> findRecentSearchesByUserId(@Param("userId") int userId);
    List<SearchHistoryWithSong> getSearchHistoryWithSongByUserId(int userId);
}
