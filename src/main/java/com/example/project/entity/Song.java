package com.example.project.entity;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    private int id;
    private String artistName;
    private String artistId;
    private String songName;
    private LocalDate releaseDate;
    private String image;
    private String preview;
    private boolean liked;
    private String songId;
    private int userId;
}
