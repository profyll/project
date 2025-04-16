package com.example.project.repository;

import com.example.project.entity.SongComment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SongCommentRepository {
    public int songCommentCreate(SongComment comment);
    public List<SongComment> getCommentsBySongId(String songId);
   ;
}
