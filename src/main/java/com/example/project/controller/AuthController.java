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

@Slf4j
@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private UserRepository userRepository;
    private KakaoApiService kakaoApiService;

    @GetMapping("/login")
    public String KakaologinHandle(Model model) {
        // log.info("loginHandle...executed");

        model.addAttribute("kakaoClientId", "4704839b7ddb306761fe09edcb6e8998");
        model.addAttribute("kakaoRedirectUri", "http://192.168.10.21:8080/auth/kakao/callback");

        return "auth/login";
    }


    @PostMapping("/login")
    public String loginPostHandle(
            @ModelAttribute LoginRequest loginRequest,
            HttpSession session,
            Model model) {
        User user =
                userRepository.findByEmail(loginRequest.getEmail());
        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            return "redirect:/index";
        } else {
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/signup")
    public String signupGetHandle(Model model) {
        model.addAttribute("kakaoClientId", "4704839b7ddb306761fe09edcb6e8998");
        model.addAttribute("kakaoRedirectUri", "http://192.168.10.21:8080/auth/kakao/callback");

        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signupPostHandle(@ModelAttribute User user) {
        User found = userRepository.findByEmail(user.getEmail());
        if (found == null) {
            userRepository.create(user);
            user.setProvider("LOCAL");
        }
        return "redirect:/home";
    }
    @GetMapping("/kakao/callback")
    public String kakaoCallbackHandle(@RequestParam("code") String code,
                                      HttpSession session
    ) throws JsonProcessingException {
        // log.info("code = {}", code);
        KakaoTokenResponse response = kakaoApiService.exchangeToken(code);
        log.info("response.idToken = {}", response.getIdToken());

        DecodedJWT decodedJWT = JWT.decode(response.getIdToken());
        String sub = decodedJWT.getClaim("sub").asString();
        String picture = decodedJWT.getClaim("picture").asString();
        String nickname = decodedJWT.getClaim("nickname").asString();

        User found = userRepository.findByProviderAndProviderId("KAKAO", sub);
        log.info("found = {}", found);
        if (found != null) {
            session.setAttribute("user", found);
        } else {
            User user = User.builder().provider("KAKAO")
                    .providerId(sub).nickname(nickname).picture(picture).build();
            userRepository.create(user);
            session.setAttribute("user", user);
        }

        return "redirect:/index";
    }
}

