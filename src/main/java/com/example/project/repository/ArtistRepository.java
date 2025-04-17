package com.example.project.repository;

import com.example.project.entity.Artist;
import com.example.project.entity.Song;
import com.example.project.entity.TopArtist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ArtistRepository {
    public int create(Artist artist);
    public int delete(String id);
    public Artist findByArtistIdAndUserId(@Param("artistId") String artistId, @Param("userId") int userId);
    public void update(@Param("artistId") String artistId,@Param("userId") int userId, @Param("liked") boolean liked);
    public Artist findByLikedAndArtistIdAndUserId(@Param("artistId") String artistId, @Param("userId") int userId);
    public List<Artist> findByLikedArtistByUserId(@Param("userId") int userId);
    public List<TopArtist> topArtist();
}
