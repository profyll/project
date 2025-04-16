package com.example.project.controller;

import com.example.project.AccessToken;
import com.example.project.entity.Song;
import com.example.project.entity.SongComment;
import com.example.project.entity.User;
import com.example.project.repository.ArtistRepository;
import com.example.project.repository.SongCommentRepository;
import com.example.project.repository.SongRepository;
import com.example.project.repository.UserRepository;
import com.example.project.vo.CommentWithNickname;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/song")
public class SongApiController {

    private SpotifyApi spotifyApi;
    private SongRepository songRepository;
    private ArtistRepository artistRepository;
    private SongCommentRepository songCommentRepository;
    private UserRepository userRepository;


    @GetMapping("/search")
    public String search(@RequestParam(value = "q", required = false) String q, Model model) {
        if (q == null || q.isBlank()) {
            return "song/search";
        }

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
    public String artist(@RequestParam("q") String artistId, Model model) {
        try {
            String token = AccessToken.CreateToken.accesstoken();

            SpotifyApi api = new SpotifyApi.Builder()
                    .setAccessToken(token)
                    .build();

            GetArtistRequest getArtistRequest = api.getArtist(artistId).build();
            Artist art = getArtistRequest.execute();

            GetArtistsTopTracksRequest topTracksRequest = api.getArtistsTopTracks(artistId, CountryCode.KR).build();
            Track[] tracks = topTracksRequest.execute();



            model.addAttribute("tracks", Arrays.asList(tracks));
            model.addAttribute("artistId", art.getId());
            model.addAttribute("name", art.getName());
            model.addAttribute("genres", art.getGenres());
            model.addAttribute("imageUrl", art.getImages().length > 0 ? art.getImages()[0].getUrl() : null);


            return "song/artist";

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/track")
    public String track(@RequestParam("q") String trackId, Model model) {
        try {
            String token = AccessToken.CreateToken.accesstoken();

            SpotifyApi api = new SpotifyApi.Builder()
                    .setAccessToken(token)
                    .build();

            GetTrackRequest getTrackRequest = api.getTrack(trackId).build();
            Track track = getTrackRequest.execute();

            Song song = Song.builder()
                    .songName(track.getName())
                    .artistName(track.getArtists()[0].getName())
                    .releaseDate(LocalDate.parse(track.getAlbum().getReleaseDate()))
                    .artistId(track.getArtists()[0].getId())
                    .image(track.getAlbum().getImages()[0].getUrl())
                    .preview(track.getPreviewUrl())
                    .liked(false)
                    .songId(track.getId())
                    .build();

            // 댓글
            List<SongComment> comments = songCommentRepository.getCommentsBySongId(trackId);
            List<CommentWithNickname> commentWithNicknames = new ArrayList<>();

            for (SongComment comment : comments) {
                User user = userRepository.findById(comment.getUserId());
                CommentWithNickname dto = new CommentWithNickname();
                dto.setComment(comment);
                dto.setNickname(user != null ? user.getNickname() : "탈퇴한 유저"); // null 방지!
                commentWithNicknames.add(dto);
            }


            model.addAttribute("comments", commentWithNicknames);
            model.addAttribute("name", track.getName());
            model.addAttribute("album", track.getAlbum().getName());
            model.addAttribute("image", track.getAlbum().getImages()[0].getUrl());
            model.addAttribute("artist", track.getArtists()[0].getName());
            model.addAttribute("preview", track.getPreviewUrl());
            model.addAttribute("songId", track.getId());

            return "song/track";

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }




    @PostMapping("/track/{songId}/comment")
    public String addComment(@RequestParam("content") String content,
                             @SessionAttribute("user") User user,
                             @PathVariable("songId") String songId,
                             Model model) {

        if (content.trim().isEmpty()) {
            model.addAttribute("error", "댓글 내용을 입력해주세요!");
            return "redirect:/song/track/" + songId;
        }

        SongComment newComment = SongComment.builder()
                .userId(user.getId())
                .content(content)
                .date(LocalDateTime.now())
                .songId(songId)
                .build();

        if (songCommentRepository.songCommentCreate(newComment) > 0) {
            System.out.println("댓글이 성공적으로 저장되었습니다.");
        } else {
            System.out.println("댓글 저장 실패");
        }

        return "redirect:/song/track?q=" + songId;
    }








}
