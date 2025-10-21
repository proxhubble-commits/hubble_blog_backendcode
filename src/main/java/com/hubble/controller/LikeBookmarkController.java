package com.hubble.controller;

import com.hubble.dto.*;
import com.hubble.service.LikeBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LikeBookmarkController {
    
    private final LikeBookmarkService likeBookmarkService;

    // ============================================
    // 노트 좋아요 토글
    // ============================================
    @PostMapping("/notes/{noteId}/like")
    public ResponseEntity<Void> toggleLike(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long noteId) {
        likeBookmarkService.toggleLike(userId, noteId);
        return ResponseEntity.ok().build();
    }

    // ============================================
    // 북마크 추가/수정
    // ============================================
    @PostMapping("/notes/{noteId}/bookmark")
    public ResponseEntity<BookmarkResponse> addBookmark(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long noteId,
            @RequestBody(required = false) BookmarkRequest request) {
        
        if (request == null) {
            request = new BookmarkRequest(); // 기본값 사용
        }
        
        BookmarkResponse response = likeBookmarkService.addOrUpdateBookmark(userId, noteId, request);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 북마크 제거
    // ============================================
    @DeleteMapping("/notes/{noteId}/bookmark")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long noteId) {
        likeBookmarkService.removeBookmark(userId, noteId);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // 좋아요/북마크 상태 조회
    // ============================================
    @GetMapping("/notes/{noteId}/status")
    public ResponseEntity<LikeBookmarkStatusResponse> getStatus(
            @PathVariable Long noteId,
            @AuthenticationPrincipal(errorOnInvalidType = false) Long userId) {
        LikeBookmarkStatusResponse response = likeBookmarkService.getStatus(userId, noteId);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 내 북마크 목록
    // ============================================
    @GetMapping("/me/bookmarks")
    public ResponseEntity<Page<BookmarkResponse>> getMyBookmarks(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String folder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BookmarkResponse> bookmarks = likeBookmarkService.getMyBookmarks(userId, folder, pageable);
        return ResponseEntity.ok(bookmarks);
    }

    // ============================================
    // 내가 좋아요한 노트 목록
    // ============================================
    @GetMapping("/me/liked")
    public ResponseEntity<Page<NoteListItemDto>> getLikedNotes(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<NoteListItemDto> notes = likeBookmarkService.getLikedNotes(userId, pageable);
        return ResponseEntity.ok(notes);
    }
}
