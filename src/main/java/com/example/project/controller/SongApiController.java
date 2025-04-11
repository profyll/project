package com.example.project.controller;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import lombok.AllArgsConstructor;
import com.example.project.AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.wrapper.spotify.model_objects.specification.Track;


import java.util.Arrays;

@Controller
@AllArgsConstructor
@RequestMapping("/song")
public class SongApiController {

    private SpotifyApi spotifyApi;

    @GetMapping("/search")
    public String searchForm(){
        return "song/search";
    }

    @PostMapping("/search")
    public String searchHandle(@RequestParam("q") String q, Model model) {
        try {
            String token = AccessToken.CreateToken.accesstoken();

            SpotifyApi api = new SpotifyApi.Builder()
                    .setAccessToken(token)
                    .build();

            SearchTracksRequest searchRequest = api.searchTracks(q).limit(10).build();
            Track[] tracks = searchRequest.execute().getItems();

            model.addAttribute("tracks", Arrays.asList(tracks));
            return "song/result";

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
