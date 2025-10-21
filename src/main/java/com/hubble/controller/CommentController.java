package com.hubble.controller;

import com.hubble.dto.*;
import com.hubble.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes/{noteId}/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;

    // ============================================
    // 댓글 작성
    // ============================================
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long noteId,
            @Valid @RequestBody CreateCommentRequest request) {
        CommentResponse response = commentService.createComment(userId, noteId, request);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 댓글 수정
    // ============================================
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long noteId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request) {
        CommentResponse response = commentService.updateComment(userId, commentId, request);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 댓글 삭제 (소프트 삭제)
    // ============================================
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long noteId,
            @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // 댓글 목록 조회
    // ============================================
    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable Long noteId,
            @AuthenticationPrincipal(errorOnInvalidType = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentResponse> comments = commentService.getComments(noteId, userId, pageable);
        return ResponseEntity.ok(comments);
    }

    // ============================================
    // 특정 댓글의 대댓글 목록
    // ============================================
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentResponse>> getReplies(
            @PathVariable Long noteId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal(errorOnInvalidType = false) Long userId) {
        
        List<CommentResponse> replies = commentService.getReplies(commentId, userId);
        return ResponseEntity.ok(replies);
    }

    // ============================================
    // 댓글 좋아요 토글
    // ============================================
    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> toggleLike(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long noteId,
            @PathVariable Long commentId) {
        commentService.toggleLike(userId, commentId);
        return ResponseEntity.ok().build();
    }
}
