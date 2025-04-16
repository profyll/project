package com.example.project.vo;

import com.example.project.entity.SongComment;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentWithNickname {
    private SongComment comment;
    private String nickname;
}
