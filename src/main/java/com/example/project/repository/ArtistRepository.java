package com.example.project.repository;

import com.example.project.entity.Artist;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ArtistRepository {
    public int create(Artist artist);
}
