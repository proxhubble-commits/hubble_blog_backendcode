package com.hubble.controller;

import com.hubble.repository.UserRepository;
import com.hubble.security.jwt.JwtTokenProvider;
import com.hubble.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenProvider jwt;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepo;

    @PostMapping("/refresh")
    public Map<String, String> refresh(@CookieValue(name = "refresh_token", required = false) String refresh) {
        Long userId = refreshTokenService.validateAndGetUserId(refresh);
        String access = jwt.createAccessToken(userId, userRepo.findById(userId).orElseThrow().getRole());
        return Map.of("accessToken", access);
    }

    @PostMapping("/logout")
    public void logout(@CookieValue(name = "refresh_token", required = false) String refresh, HttpServletResponse res) {
        refreshTokenService.invalidate(refresh);
        Cookie c = new Cookie("refresh_token", "");
        c.setMaxAge(0); c.setPath("/"); c.setHttpOnly(true); c.setSecure(true);
        res.addCookie(c);
    }
}
