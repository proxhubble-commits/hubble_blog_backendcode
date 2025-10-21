package com.hubble.controller;

import com.hubble.dto.*;
import com.hubble.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;

    // ============================================
    // 작성자 구독
    // ============================================
    @PostMapping("/users/{authorId}")
    public ResponseEntity<Void> subscribe(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long authorId) {
        subscriptionService.subscribe(userId, authorId);
        return ResponseEntity.ok().build();
    }

    // ============================================
    // 구독 취소
    // ============================================
    @DeleteMapping("/users/{authorId}")
    public ResponseEntity<Void> unsubscribe(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long authorId) {
        subscriptionService.unsubscribe(userId, authorId);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // 구독 상태 조회
    // ============================================
    @GetMapping("/users/{authorId}/status")
    public ResponseEntity<SubscriptionStatusResponse> getStatus(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long authorId) {
        SubscriptionStatusResponse response = subscriptionService.getSubscriptionStatus(userId, authorId);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 내가 구독한 작성자 목록
    // ============================================
    @GetMapping("/me")
    public ResponseEntity<Page<SubscriptionResponse>> getMySubscriptions(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SubscriptionResponse> subscriptions = subscriptionService.getMySubscriptions(userId, pageable);
        return ResponseEntity.ok(subscriptions);
    }

    // ============================================
    // 나를 구독한 사람 목록
    // ============================================
    @GetMapping("/subscribers")
    public ResponseEntity<Page<SubscriberResponse>> getMySubscribers(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SubscriberResponse> subscribers = subscriptionService.getMySubscribers(userId, pageable);
        return ResponseEntity.ok(subscribers);
    }
}
