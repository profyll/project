package com.example.project.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSong {
    private String songId;
    private String songName;
    private String artistName;
    private int likeCount;
    private String image;
}
