package com.example.project.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class SearchHistoryWithSong {
    private String songId;
    private int userId;
    private LocalDateTime searchedAt;
    private String songName;
    private String image;
}
