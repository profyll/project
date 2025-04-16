package com.example.project.repository;

import com.example.project.entity.Song;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface SongRepository {
    public int create(Song song);
    public int delete(String id);
    public Song findBySongId(String songId);
    public void update(@Param("songId") String songId, @Param("liked") boolean liked);
}
