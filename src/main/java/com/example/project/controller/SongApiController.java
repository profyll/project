package com.example.project.controller;

import com.example.project.AccessToken;
import com.example.project.entity.SearchHistory;
import com.example.project.entity.Song;
import com.example.project.repository.ArtistRepository;
import com.example.project.repository.SearchHistoryRepository;
import com.example.project.repository.SongRepository;
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
import java.util.Arrays;

@Controller
@AllArgsConstructor
@RequestMapping("/song")
public class SongApiController {

    private SpotifyApi spotifyApi;
    private SongRepository songRepository;
    private ArtistRepository artistRepository;
    private SearchHistoryRepository searchHistoryRepository;

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

            com.example.project.entity.Artist existing = artistRepository.findByArtistId(artistId);
            boolean liked = existing != null && existing.isLiked();

            model.addAttribute("tracks", Arrays.asList(tracks));
            model.addAttribute("artistId", art.getId());
            model.addAttribute("name", art.getName());
            model.addAttribute("genres", art.getGenres());
            model.addAttribute("imageUrl", art.getImages().length > 0 ? art.getImages()[0].getUrl() : null);
            model.addAttribute("liked", liked);

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

            SearchHistory searchHistory = SearchHistory.builder()
                    .songName(track.getName())
                    .songId(track.getId())
                    .build();

            searchHistoryRepository.create(searchHistory);

            com.example.project.entity.Song existing = songRepository.findBySongId(trackId);
            boolean liked = existing != null && existing.isLiked();

            model.addAttribute("name", track.getName());
            model.addAttribute("album", track.getAlbum().getName());
            model.addAttribute("image", track.getAlbum().getImages()[0].getUrl());
            model.addAttribute("artist", track.getArtists()[0].getName());
            model.addAttribute("preview", track.getPreviewUrl());
            model.addAttribute("songId", track.getId());
            model.addAttribute("liked", liked);

            return "song/track";

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/artist-update-liked")
    public String updateArtistLiked(@RequestParam("artistId") String artistId, @RequestParam("liked") String likedStr, Model model) {
        try {
            boolean liked = Boolean.parseBoolean(likedStr);

            com.example.project.entity.Artist existing = artistRepository.findByArtistId(artistId);

            if (existing != null) {
                artistRepository.update(artistId, !liked);
            } else {
                String token = AccessToken.CreateToken.accesstoken();

                SpotifyApi api = new SpotifyApi.Builder()
                        .setAccessToken(token)
                        .build();

                GetArtistRequest getArtistRequest = api.getArtist(artistId).build();
                Artist art = getArtistRequest.execute();

                com.example.project.entity.Artist artist = com.example.project.entity.Artist.builder()
                        .artistId(artistId)
                        .artistName(art.getName())
                        .genre(art.getGenres().length > 0 ? art.getGenres()[0] : "Unknown")
                        .image(art.getImages().length > 0 ? art.getImages()[0].getUrl() : "https://via.placeholder.com/300") // ✅ 수정된 부분
                        .liked(true)
                        .build();
                artistRepository.create(artist);
            }

            return "redirect:/song/artist?q=" + artistId;

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("e", e);
            return "error";
        }
    }

    @GetMapping("/track-update-liked")
    public String updateTrackLiked(@RequestParam("songId") String songId,
                                   @RequestParam("liked") String likedStr,
                                   Model model) {
        try {
            boolean liked = Boolean.parseBoolean(likedStr);

            Song existing = songRepository.findBySongId(songId);

            if (existing != null) {
                songRepository.update(songId, !liked);
            } else {
                String token = AccessToken.CreateToken.accesstoken();

                SpotifyApi api = new SpotifyApi.Builder()
                        .setAccessToken(token)
                        .build();

                GetTrackRequest getTrackRequest = api.getTrack(songId).build();
                Track track = getTrackRequest.execute();

                Song song = Song.builder()
                        .songId(songId)
                        .songName(track.getName())
                        .artistName(track.getArtists()[0].getName())
                        .artistId(track.getArtists()[0].getId())
                        .releaseDate(LocalDate.parse(track.getAlbum().getReleaseDate()))
                        .image(track.getAlbum().getImages()[0].getUrl())
                        .preview(track.getPreviewUrl())
                        .liked(true)
                        .build();

                songRepository.create(song);
            }

            return "redirect:/song/track?q=" + songId;

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("e", e);
            return "error";
        }
    }

}
