package com.example.project.repository;

import com.example.project.entity.Song;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface SongRepository {
    public int create(Song song);
    public int delete(String id);
    public Song findBySongId(String songId);
    public Song findBySongIdAndUserId(@Param("songId") String songId, @Param("userId") int userId);
    public void update(@Param("songId") String songId, @Param("userId") int userId, @Param("liked") boolean liked);
    public List<Song> findByLikedSongByUserId(@Param("userId") int userId);
}
