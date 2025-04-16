package com.example.project.repository;

import com.example.project.entity.Artist;
import com.example.project.entity.Song;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ArtistRepository {
    public int create(Artist artist);
    public int delete(String id);
    public Artist findByArtistIdAndUserId(@Param("artistId") String artistId, @Param("userId") int userId);
    public void update(@Param("artistId") String artistId,@Param("userId") int userId, @Param("liked") boolean liked);
}
