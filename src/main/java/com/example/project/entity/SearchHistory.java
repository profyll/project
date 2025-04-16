package com.example.project.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistory {
        private int id;
        private String songId;
        private String songName;
        private LocalDateTime searchedAt;
        private int userId;
        private String image;
    }

