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
import lombok.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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

    @GetMapping("/edit")
    public String editProfilePage(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser != null) {
            User freshUser = userRepository.findById(sessionUser.getId());
            model.addAttribute("user", freshUser);
            session.setAttribute("user", freshUser);
        } else {
            // 로그인 안 했으면 무조건 인덱스 페이지로 리다이렉트
            return "redirect:/index";
        }

        return "user/edit";
    }




    @PostMapping("/edit/go")
    public String updateUserInfo(@RequestParam String name,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) String gender,
                                 @RequestParam(required = false) String currentPassword,
                                 @RequestParam(required = false) String newPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");

        if (user != null) {
            // 비밀번호 변경 요청이 있는 경우
            if (currentPassword != null && !currentPassword.isBlank()) {
                if (!currentPassword.equals(user.getPassword())) {
                    redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
                    return "redirect:/user/edit"; // 다시 수정페이지로
                }
            }

            // 빈 문자열은 null로 설정
            if (email != null && email.trim().isEmpty()) {
                email = null;
            }
            if (gender != null && gender.trim().isEmpty()) {
                gender = null;
            }

            // 새 비밀번호가 있을 경우만 업데이트
            String finalPassword = (newPassword != null && !newPassword.isBlank()) ? newPassword : user.getPassword();

            userRepository.updateUserInfo(
                    user.getId(),
                    name,
                    email,
                    gender,
                    finalPassword
            );

            // 세션 정보 업데이트
            user.setNickname(name);
            user.setEmail(email);
            user.setGender(gender);
            user.setPassword(finalPassword);

            session.setAttribute("user", user);
        }

        return "redirect:/user/mypage";
    }











    @PostMapping("/mypage/upload-image")
    @ResponseBody
    public Map<String, String> uploadProfileImage(
            @SessionAttribute("user") Optional<User> user,
            @RequestParam("image") MultipartFile imageFile) {

        Map<String, String> result = new HashMap<>();

        try {
            if (user.isEmpty() || imageFile.isEmpty()) {
                result.put("error", "사용자 또는 이미지 없음");
                return result;
            }

            User u = user.get();

            // 기존 이미지 삭제 (기본 이미지 제외)
            String currentImage = u.getImage();
            if (currentImage != null && currentImage.startsWith("/uploads/")) {
                Path oldImagePath = Paths.get("src/main/resources/static" + currentImage);
                Files.deleteIfExists(oldImagePath);
            }

            // 확장자 추출
            String originalFilename = imageFile.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }

            if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)")) {
                result.put("error", "지원하지 않는 이미지 형식입니다.");
                return result;
            }


            // 저장할 실제 경로 - 서버에서 접근 가능한 외부 경로
            String uploadDir = "/home/ubuntu/app/images/";  // 이건 서버 운영체제에 맞게 설정

            // 새 파일 이름 생성
            String fileName = "user_" + u.getId() + "_" + UUID.randomUUID() + extension;
            Path uploadPath = Paths.get(uploadDir + fileName);  // 여기 경로 바꿈
            Files.createDirectories(uploadPath.getParent());
            Files.write(uploadPath, imageFile.getBytes());

            String imageUrl = "/uploads/" + fileName;


            // DB 반영
            u.setImage(imageUrl);
            userRepository.updateImage(u.getId(), imageUrl);

            result.put("imageUrl", imageUrl);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", "이미지 업로드 실패: " + e.getMessage());
            return result;
        }
    }

    @Configuration
    public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:/home/ubuntu/app/images/");
        }
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

