package com.hubble.controller;

import com.hubble.dto.*;
import com.hubble.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    
    private final ProfileService profileService;

    // ============================================
    // 프로필 조회
    // ============================================
    @GetMapping("/users/{userId}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable Long userId) {
        ProfileResponse response = profileService.getProfile(userId);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 내 프로필 조회
    // ============================================
    @GetMapping("/me/profile")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal Long userId) {
        ProfileResponse response = profileService.getProfile(userId);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 프로필 수정
    // ============================================
    @PutMapping("/me/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = profileService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 스킬 추가
    // ============================================
    @PostMapping("/me/skills")
    public ResponseEntity<UserSkillDto> addSkill(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AddSkillRequest request) {
        UserSkillDto skill = profileService.addSkill(userId, request);
        return ResponseEntity.ok(skill);
    }

    // ============================================
    // 스킬 삭제
    // ============================================
    @DeleteMapping("/me/skills/{skillId}")
    public ResponseEntity<Void> removeSkill(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long skillId) {
        profileService.removeSkill(userId, skillId);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // 관심사 추가 (최대 2개)
    // ============================================
    @PostMapping("/me/interests")
    public ResponseEntity<InterestDto> addInterest(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AddInterestRequest request) {
        InterestDto interest = profileService.addInterest(userId, request);
        return ResponseEntity.ok(interest);
    }

    // ============================================
    // 관심사 삭제
    // ============================================
    @DeleteMapping("/me/interests/{interestId}")
    public ResponseEntity<Void> removeInterest(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long interestId) {
        profileService.removeInterest(userId, interestId);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // 전체 관심사 목록 (선택 가능한 3가지)
    // ============================================
    @GetMapping("/interests")
    public ResponseEntity<List<InterestDto>> getAllInterests() {
        List<InterestDto> interests = profileService.getAllInterests();
        return ResponseEntity.ok(interests);
    }

    // ============================================
    // 설정 조회
    // ============================================
    @GetMapping("/me/settings")
    public ResponseEntity<SettingsResponse> getSettings(@AuthenticationPrincipal Long userId) {
        SettingsResponse response = profileService.getSettings(userId);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 설정 수정
    // ============================================
    @PutMapping("/me/settings")
    public ResponseEntity<SettingsResponse> updateSettings(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateSettingsRequest request) {
        SettingsResponse response = profileService.updateSettings(userId, request);
        return ResponseEntity.ok(response);
    }
}
