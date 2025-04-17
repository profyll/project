package com.example.project.controller;

import com.example.project.entity.Artist;
import com.example.project.entity.TopArtist;
import com.example.project.entity.TopSong;
import com.example.project.entity.User;
import com.example.project.repository.ArtistRepository;
import com.example.project.repository.SongRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
}
