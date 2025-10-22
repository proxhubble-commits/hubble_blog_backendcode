package com.hubble.service;

import com.hubble.entity.RefreshToken;
import com.hubble.entity.User;
import com.hubble.repository.RefreshTokenRepository;
import com.hubble.repository.UserRepository;
import com.hubble.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository repo;
    private final UserRepository userRepo;
    private final JwtTokenProvider jwt;

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void store(Long userId, String refreshToken) {
        User user = userRepo.findById(userId).orElseThrow();
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setTokenHash(sha256(refreshToken));
        rt.setExpiresAt(OffsetDateTime.now().plusDays(14));
        repo.save(rt);
    }

    public Long validateAndGetUserId(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Missing refresh token");
        }
        Optional<RefreshToken> found = repo.findByTokenHash(sha256(refreshToken));
        RefreshToken rt = found.orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        if (rt.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Expired refresh token");
        }
        return rt.getUser().getId();
    }

    public void invalidate(String refreshToken) {
        if (refreshToken != null) {
            repo.deleteByTokenHash(sha256(refreshToken));
        }
    }
}
