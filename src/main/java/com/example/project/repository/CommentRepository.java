package com.example.project.repository;

import com.example.project.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentRepository {
    public int commentCreate(Comment comment);
    public List<Comment> getCommentsBySongId(int songId);
    public List<Comment> getCommentsByArtistId(int artistId);
}
