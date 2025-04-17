package com.example.project.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopArtist {
    private String artistId;
    private String artistName;
    private String image;
    private String genre;
    private int likeCount;
}
