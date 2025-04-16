package com.example.project.controller;

import com.example.project.AccessToken;
import com.example.project.entity.Comment;
import com.example.project.entity.Song;
import com.example.project.entity.User;
import com.example.project.repository.ArtistRepository;
import com.example.project.repository.CommentRepository;
import com.example.project.repository.SongRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.requests.data.albums.GetAlbumsTracksRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchArtistsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.ParseException;

import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.wrapper.spotify.model_objects.specification.Track;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/song")
public class SongApiController {

    private SpotifyApi spotifyApi;
    private SongRepository songRepository;
    private ArtistRepository artistRepository;
    private CommentRepository commentRepository;

    @GetMapping("/search")
    public String searchForm() {
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

    @GetMapping("/artist")
    public String artistForm() {
        return "song/artist";
    }

    @PostMapping("/artist")
    public String artistHandle(@RequestParam("q") String q, Model model) {
        try {
            String token = AccessToken.CreateToken.accesstoken();

            SpotifyApi api = new SpotifyApi.Builder()
                    .setAccessToken(token)
                    .build();

            GetArtistRequest getArtistRequest = api.getArtist(q).build();
            Artist artist = getArtistRequest.execute();

            GetArtistsTopTracksRequest topTracksRequest = api.getArtistsTopTracks(q, CountryCode.KR).build();
            Track[] tracks = topTracksRequest.execute();

            model.addAttribute("tracks", Arrays.asList(tracks));
            model.addAttribute("name", artist.getName());
            model.addAttribute("genres", artist.getGenres());
            model.addAttribute("imageUrl", artist.getImages().length > 0 ? artist.getImages()[0].getUrl() : null);

            return "song/artist";

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/track")
    public String trackForm() {
        return "song/track";
    }

    @PostMapping("/track")
    public String trackHandle(Model model, @RequestParam("q") String q) {
        try {
            String token = AccessToken.CreateToken.accesstoken();

            SpotifyApi api = new SpotifyApi.Builder()
                    .setAccessToken(token)
                    .build();

            GetTrackRequest getTrackRequest = api.getTrack(q).build();
            Track track = getTrackRequest.execute();

           Song song = Song.builder().songName(track.getName()).
                   artistName(track.getArtists()[0].getName()).
                   releaseDate(LocalDate.parse(track.getAlbum().getReleaseDate())).
                   artistId(track.getArtists()[0].getId()).
                   image(track.getAlbum().getImages()[0].getUrl()).
                   preview(track.getPreviewUrl()).
                   liked(false).
                   songId(track.getId()).
                   build();

           songRepository.create(song);


            model.addAttribute("name", track.getName());
            model.addAttribute("album", track.getAlbum().getName());
            model.addAttribute("image", track.getAlbum().getImages()[0].getUrl());
            model.addAttribute("artist", track.getArtists()[0].getName());
            model.addAttribute("preview", track.getPreviewUrl());
            model.addAttribute("songId", track.getId());
            model.addAttribute("liked", song.isLiked());

            return "song/track";

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }


    @GetMapping("/track/detail")
    public String getTrack(@RequestParam("songId") String songId, Model model) throws JsonProcessingException {
        if (songId == null) {
            return "redirect:/"; // 또는 기본 song 페이지
        }

        List<Comment> comments = commentRepository.getCommentsBySongId(songId);
        model.addAttribute("comments", commentRepository.getCommentsBySongId(songId));
        model.addAttribute("songId", songId);

        return "song/track";
    }


    @PostMapping("/song/track/{songId}/{comment}")
    public String addComment(@PathVariable("comment") Comment comment,
                             @SessionAttribute("user") User user,
                             @PathVariable("songId") String songId, Model model) throws JsonProcessingException {
        System.out.println("songId: " + songId);
        System.out.println("comment: " + comment.getContent());

        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            model.addAttribute("error", "댓글 내용을 입력해주세요!");
            return "song/track";
        }

        // 댓글 작성
        Comment newComment = Comment.builder()
                .userId(user.getId())
                .songId(songId)
                .content(comment.getContent())
                .date(LocalDateTime.now())
                .build();

        commentRepository.commentCreate(newComment);
        return "redirect:/song/track/" + songId;
    }






}
