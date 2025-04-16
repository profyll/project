package com.example.project.controller;

import com.example.project.entity.SearchHistory;
import com.example.project.entity.Song;
import com.example.project.entity.User;
import com.example.project.repository.SearchHistoryRepository;
import com.example.project.repository.UserRepository;
import com.example.project.vo.SearchHistoryWithSong;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@RequestMapping("/user")

public class UserController {
    private UserRepository userRepository;
    private SearchHistoryRepository searchHistoryRepository;

    @GetMapping("/mypage")
    public String mypageHandle(
            @SessionAttribute(name = "user", required = false) User user,
            Model model) {

        if (user == null) {
            return "redirect:/index";
        }

        int userId = user.getId();

        List<Song> likedSongs = userRepository.findLikedSongsByUserId(userId);
        List<SearchHistoryWithSong> rawHistory = searchHistoryRepository.getSearchHistoryWithSongByUserId(userId);

        System.out.println("userId = " + userId);
        for (SearchHistoryWithSong h : rawHistory) {
            System.out.println(h.getSongName() + " / " + h.getSearchedAt());
        }
        model.addAttribute("user", user);
        model.addAttribute("likedSongs", likedSongs);
        model.addAttribute("searchHistory", rawHistory);

        return "user/mypage";
    }




    @GetMapping("/logout")
    public String logout(@SessionAttribute("user") Optional<User> user, HttpSession session, Model model) {
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            session.invalidate();
        } else {
            return "redirect:/index";
        }
        return "redirect:/index";
    }
}

