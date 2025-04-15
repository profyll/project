package com.example.project.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class Comment {
    private int id;
    private int userId;
    private String content;
    private LocalDateTime date;
    private Integer songId;
    private Integer  artistId;
}
