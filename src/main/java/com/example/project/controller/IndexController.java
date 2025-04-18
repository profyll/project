package com.example.project.controller;

import com.example.project.AccessToken;
import com.example.project.entity.*;
import com.example.project.repository.ArtistRepository;
import com.example.project.repository.SongRepository;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.search.SearchItemRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@Controller
public class IndexController {
    private ArtistRepository artistRepository;
    private SongRepository songRepository;

    @GetMapping({"/", "/index"})
    public String indexHandle(@SessionAttribute("user") Optional<User> user) {

        if (user.isEmpty()) {
            return "index";
        } else {
            return "redirect:/home";
        }
    }

    @GetMapping("/home")
    public String homeHandle(@SessionAttribute("user") Optional<User> user, Model model) {

        if (user.isPresent()) {

            model.addAttribute("user", user.get());

            List<Artist> artistLiked = artistRepository.findByLikedArtistByUserId(user.get().getId());

            model.addAttribute("likedA",artistLiked);

            List<TopSong> topSongs = songRepository.getTop10LikedSongs();

            model.addAttribute("topSong", topSongs);

            List<TopArtist> topArtists = artistRepository.topArtist();

            model.addAttribute("topArist", topArtists);

            return "home";
        } else {
            return "redirect:/index";
        }
    }


    @GetMapping("/recommend/random")
    @ResponseBody
    public Map<String, Object> getRandomRecommendation(@SessionAttribute("user") User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = AccessToken.CreateToken.accesstoken();

            SpotifyApi api = new SpotifyApi.Builder()
                    .setAccessToken(token)
                    .build();

            // 랜덤 키워드 생성 (알파벳 a~z 중 하나)
            char randomChar = (char) ('a' + new Random().nextInt(26));
            String randomQuery = String.valueOf(randomChar);

            // Spotify에서 트랙 검색
            SearchItemRequest searchRequest = api.searchItem(randomQuery, "track")
                    .limit(20) // 최대 20곡
                    .build();

            Paging<Track> trackPaging = searchRequest.execute().getTracks();
            Track[] tracks = trackPaging.getItems();

            if (tracks.length == 0) {
                response.put("error", "추천할 곡이 없습니다.");
                return response;
            }

            // 랜덤 트랙 하나 선택
            Track selected = tracks[new Random().nextInt(tracks.length)];

            response.put("name", selected.getName());
            response.put("artist", selected.getArtists()[0].getName());
            response.put("image", selected.getAlbum().getImages()[0].getUrl());
            response.put("preview", selected.getPreviewUrl() != null ? selected.getPreviewUrl() : "");
            response.put("id", selected.getId());

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "추천 실패");
            return response;
        }
    }



}
