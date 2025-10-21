package com.hubble.controller;

import com.hubble.dto.*;
import com.hubble.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;

    // ============================================
    // 알림 목록 조회
    // ============================================
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = notificationService.getNotifications(userId, isRead, pageable);
        return ResponseEntity.ok(notifications);
    }

    // ============================================
    // 읽지 않은 알림 개수
    // ============================================
    @GetMapping("/unread/count")
    public ResponseEntity<NotificationCountResponse> getUnreadCount(@AuthenticationPrincipal Long userId) {
        NotificationCountResponse response = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 알림 읽음 처리 (단건)
    // ============================================
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return ResponseEntity.ok().build();
    }

    // ============================================
    // 모든 알림 읽음 처리
    // ============================================
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // ============================================
    // 알림 삭제
    // ============================================
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long notificationId) {
        notificationService.deleteNotification(userId, notificationId);
        return ResponseEntity.noContent().build();
    }
}
