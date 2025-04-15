package com.example.project.controller;

import com.example.project.entity.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/user")

public class UserController {
    @GetMapping("/mypage")
    public String mypageHandle(@SessionAttribute("user") Optional<User> user, Model model) {
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        } else {
            return "redirect:/index";
        }
        return "user/mypage";
    }
}
