package com.example.project.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("song/")
public class CommentController {
    @GetMapping("/comment")
    public String commentHandle() {


        return "song/datail/comment";
    }
}
