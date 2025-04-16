package com.example.project.repository;

import com.example.project.entity.Artist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ArtistRepository {
    public int create(Artist artist);
    public int delete(String id);
    public Artist findByArtistId(String artistId);
    public void update(@Param("artistId") String artistId, @Param("liked") boolean liked);
}
