package com.example.project.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class YoutubeItem {

    @Setter
    @Getter
    public class Id {
      private String kind;
      private String videoId;
    }


    private String kind;
    private String etag;
    private Id id;
}
