package com.hubble.controller;

import com.hubble.dto.*;
import com.hubble.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;

    // ============================================
    // Dashboard 요약 통계
    // ============================================
    @GetMapping
    public ResponseEntity<DashboardSummaryResponse> getDashboard(@AuthenticationPrincipal Long userId) {
        DashboardSummaryResponse response = dashboardService.getDashboardSummary(userId);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 특정 노트 상세 통계
    // ============================================
    @GetMapping("/notes/{noteId}")
    public ResponseEntity<NoteStatsResponse> getNoteStats(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long noteId) {
        NoteStatsResponse response = dashboardService.getNoteStats(userId, noteId);
        return ResponseEntity.ok(response);
    }
}
