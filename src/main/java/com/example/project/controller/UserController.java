package com.example.project.controller;


import com.example.project.repository.ArtistRepository;
import com.example.project.repository.SongRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/user")
@Controller
public class UserController {
    private ArtistRepository artistRepository;
    private SongRepository songRepository;

}
