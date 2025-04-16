package com.example.project.controller;

import com.example.project.entity.SearchHistory;
import com.example.project.entity.Song;
import com.example.project.entity.User;
import com.example.project.repository.SearchHistoryRepository;
import com.example.project.repository.SongRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/user")

public class UserController {

    private SongRepository songRepository;

    @GetMapping("/mypage")
    public String mypageHandle(@SessionAttribute("user") Optional<User> user, Model model) {
        if (user.isPresent()) {
            model.addAttribute("user", user.get());

            List<Song> liked = songRepository.findByLikedSongByUserId(user.get().getId());

            model.addAttribute("liked",liked);

        } else {
            return "redirect:/index";
        }
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

