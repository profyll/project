package com.example.project.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.project.repository.UserRepository;
import com.example.project.entity.User;
import com.example.project.request.LoginRequest;
import com.example.project.service.KakaoApiService;
import com.example.project.vo.KakaoTokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private UserRepository userRepository;
    private KakaoApiService kakaoApiService;

    @GetMapping("/login")
    public String KakaologinHandle(Model model) throws UnknownHostException {
        // log.info("loginHandle...executed");

        String currentIp = InetAddress.getLocalHost().getHostAddress();
        model.addAttribute("kakaoClientId", "4704839b7ddb306761fe09edcb6e8998");
        model.addAttribute("kakaoRedirectUri", "http://43.200.179.171:8080:8080/auth/kakao/callback");

        return "auth/login";
    }


    @PostMapping("/login")
    public String loginPostHandle(
            @ModelAttribute LoginRequest loginRequest,
            HttpSession session,
            Model model) {

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        // 입력값 검증
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("loginError", "이메일과 비밀번호를 입력해주세요.");
            return "auth/login"; // 에러 메시지를 보여주며 다시 로그인 페이지로
        }

        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            return "redirect:/index";
        } else {
            model.addAttribute("loginError", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "auth/login";
        }
    }


    @GetMapping("/signup")
    public String signupGetHandle(Model model) {
        model.addAttribute("kakaoClientId", "4704839b7ddb306761fe09edcb6e8998");
        try {
            String currentIp = InetAddress.getLocalHost().getHostAddress();
            model.addAttribute("kakaoRedirectUri", "http://43.200.179.171:8080:8080/auth/kakao/callback");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            model.addAttribute("kakaoRedirectUri", "http://localhost:8080/auth/kakao/callback"); // fallback
        }
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signupPostHandle(@ModelAttribute User user, Model model, HttpSession session) {
        String email = user.getEmail();
        String password = user.getPassword();
        String name = user.getNickname();

        // 입력값 검증
        if (email == null || email.isBlank() ||
                password == null || password.isBlank() ||
                name == null || name.isBlank()) {

            model.addAttribute("signupError", "모든 필수 정보를 입력해주세요.");
            return "auth/signup";
        }

        User found = userRepository.findByEmail(email);
        if (found == null) {
            user.setProvider("LOCAL");
            userRepository.create(user);

            // 세션에 로그인 정보 저장
            session.setAttribute("user", user);

            return "redirect:/home";
        } else {
            model.addAttribute("signupError", "이미 존재하는 이메일입니다.");
            return "auth/signup";
        }
    }



    @GetMapping("/kakao/callback")
    public String kakaoCallbackHandle(@RequestParam("code") String code,
                                      HttpSession session
    ) throws JsonProcessingException, UnknownHostException {

        KakaoTokenResponse response = kakaoApiService.exchangeToken(code);
        log.info("response = {}", response);

        DecodedJWT decodedJWT = JWT.decode(response.getIdToken());
        String sub = decodedJWT.getClaim("sub").asString();
        String picture = decodedJWT.getClaim("picture").asString();
        String nickname = decodedJWT.getClaim("nickname").asString();

        User found = userRepository.findByProviderAndProviderId("KAKAO", sub);
        log.info("found = {}", found);

        if (found != null) {
            session.setAttribute("user", found);
        } else {
            User user = User.builder()
                    .provider("KAKAO")
                    .providerId(sub)
                    .nickname(nickname)
                    .image(picture)
                    .build();

            userRepository.create(user);  // DB에 INSERT

            User savedUser = userRepository.findByProviderAndProviderId("KAKAO", sub);
            session.setAttribute("user", savedUser);

        }


        return "redirect:/home";
    }
}

