package com.hubble.controller;

import com.hubble.dto.*;
import com.hubble.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {
    
    private final MediaService mediaService;

    // ============================================
    // 이미지 업로드
    // ============================================
    @PostMapping("/upload/image")
    public ResponseEntity<MediaUploadResponse> uploadImage(
            @AuthenticationPrincipal Long userId,
            @RequestParam("file") MultipartFile file) throws IOException {
        MediaUploadResponse response = mediaService.uploadImage(userId, file);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 미디어 업로드 (일반)
    // ============================================
    @PostMapping("/upload")
    public ResponseEntity<MediaUploadResponse> uploadMedia(
            @AuthenticationPrincipal Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "OTHER") String type) throws IOException {
        MediaUploadResponse response = mediaService.uploadMedia(userId, file, type);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 미디어 삭제
    // ============================================
    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> deleteMedia(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long mediaId) throws IOException {
        mediaService.deleteMedia(userId, mediaId);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // 내 미디어 목록
    // ============================================
    @GetMapping("/me")
    public ResponseEntity<Page<MediaListItemDto>> getMyMedia(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MediaListItemDto> media = mediaService.getMyMedia(userId, type, pageable);
        return ResponseEntity.ok(media);
    }
}
