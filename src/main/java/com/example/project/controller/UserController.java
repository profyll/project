package com.example.project.controller;

import com.example.project.entity.Artist;
import com.example.project.entity.SearchHistory;
import com.example.project.entity.Song;
import com.example.project.entity.User;
import com.example.project.repository.ArtistRepository;
import com.example.project.repository.SearchHistoryRepository;

import com.example.project.repository.UserRepository;
import com.example.project.vo.SearchHistoryWithSong;
import com.example.project.repository.SongRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Controller
@AllArgsConstructor
@RequestMapping("/user")

public class UserController {

    private UserRepository userRepository;
    private SearchHistoryRepository searchHistoryRepository;
    private SongRepository songRepository;
    private ArtistRepository artistRepository;

    @GetMapping("/mypage")
    public String mypageHandle(@SessionAttribute("user") Optional<User> user, Model model) {

        if (user.isEmpty()) {
            return "redirect:/index";
        }

        User u = user.get();
        model.addAttribute("user", u);


        // 좋아요 (중복된 거 같음)
        List<Song> liked = songRepository.findByLikedSongByUserId(u.getId());
        model.addAttribute("liked", liked);

        // 검색 기록
        List<SearchHistoryWithSong> rawHistory = searchHistoryRepository.getSearchHistoryWithSongByUserId(u.getId());
        model.addAttribute("searchHistory", rawHistory);

        List<Artist> artistLiked = artistRepository.findByLikedArtistByUserId(user.get().getId());

        model.addAttribute("likedA",artistLiked);


        // 이미지 경로 설정
        String imagePath = (u.getImage() != null && !u.getImage().isEmpty())
                ? u.getImage()
                : "/icons/profile.png";
        model.addAttribute("image", imagePath);

        // 캐시 방지를 위한 timestamp
        model.addAttribute("imageVersion", System.currentTimeMillis());

        return "user/mypage";
    }




    @PostMapping("/mypage/upload-image")
    @ResponseBody
    public Map<String, String> uploadProfileImage(@SessionAttribute("user") Optional<User> user,
                                                  @RequestParam("image") MultipartFile imageFile) throws IOException {
        Map<String, String> result = new HashMap<>();

        if (user.isEmpty() || imageFile.isEmpty()) {
            result.put("error", "사용자 또는 이미지 없음");
            return result;
        }

        User u = user.get();

        // 기본 이미지 경로는 삭제 금지
        String currentImage = u.getImage();
        if (currentImage != null && currentImage.startsWith("/uploads/")) {
            Path oldImagePath = Paths.get("src/main/resources/static" + currentImage);
            Files.deleteIfExists(oldImagePath);
        }

        // 새 이미지 저장
        String fileName = "user_" + u.getId() + "_" + UUID.randomUUID() + ".jpg";
        Path uploadPath = Paths.get("src/main/resources/static/uploads/" + fileName);
        Files.createDirectories(uploadPath.getParent());
        Files.write(uploadPath, imageFile.getBytes());
        System.out.println("업로드 경로: " + uploadPath.toAbsolutePath());


        // DB 업데이트
        String imageUrl = "/uploads/" + fileName;
        u.setImage(imageUrl);
        userRepository.updateImage(u.getId(), imageUrl);

        System.out.println("리턴할 이미지 경로: " + imageUrl);

        result.put("imageUrl", imageUrl); // 프론트에서 이걸로 갱신
        return result;
    }





    @PostMapping("/mypage/delete-image")
    @ResponseBody
    public Map<String, String> resetToDefaultProfileImage(@SessionAttribute("user") Optional<User> user, HttpSession session) throws IOException {
        Map<String, String> result = new HashMap<>();

        if (user.isEmpty()) {
            result.put("error", "사용자 없음");
            return result;
        }

        User u = user.get();

        // 기존 이미지 삭제 (기본 이미지가 아니면)
        String currentImage = u.getImage();
        if (currentImage != null && currentImage.startsWith("/uploads/")) {
            Path oldImagePath = Paths.get("src/main/resources/static" + currentImage);
            Files.deleteIfExists(oldImagePath);
        }


        // 기본 이미지로 변경
        String defaultImagePath = "/icons/profile.png";
        u.setImage(defaultImagePath);
        userRepository.updateImage(u.getId(), defaultImagePath);

        // 세션도 업데이트
        session.setAttribute("user", u);

        result.put("imageUrl", defaultImagePath); // 프론트에서 이걸로 이미지 교체
        return result;
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

    @GetMapping("/liked")
    public String likedHandle(@SessionAttribute("user") Optional<User> user, Model model){

        if (user.isPresent()){
            List<Song> songs = songRepository.findByLikedSongByUserId(user.get().getId());
            model.addAttribute("likeSong", songs);

            List<Artist> artists = artistRepository.findByLikedArtistByUserId(user.get().getId());
            model.addAttribute("likeArtist", artists);
        }else {
            return "redirect:/index";
        }
        return "user/liked";
    }
}

