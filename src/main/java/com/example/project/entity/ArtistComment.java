package com.example.project.entity;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtistComment {
    private int id;
    private int userId;
    private String content;
    private LocalDateTime date;
    private String songId;
}
