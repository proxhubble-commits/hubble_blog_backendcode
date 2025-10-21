package com.hubble.controller;

import com.hubble.dto.*;
import com.hubble.service.SeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/series")
@RequiredArgsConstructor
public class SeriesController {
    
    private final SeriesService seriesService;

    // ============================================
    // 시리즈/폴더 생성
    // ============================================
    @PostMapping
    public ResponseEntity<SeriesResponse> createSeries(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateSeriesRequest request) {
        SeriesResponse response = seriesService.createSeries(userId, request);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 시리즈/폴더 수정
    // ============================================
    @PutMapping("/{seriesId}")
    public ResponseEntity<SeriesResponse> updateSeries(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long seriesId,
            @Valid @RequestBody UpdateSeriesRequest request) {
        SeriesResponse response = seriesService.updateSeries(userId, seriesId, request);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 시리즈/폴더 삭제
    // ============================================
    @DeleteMapping("/{seriesId}")
    public ResponseEntity<Void> deleteSeries(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long seriesId) {
        seriesService.deleteSeries(userId, seriesId);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // 시리즈/폴더 조회 (단건)
    // ============================================
    @GetMapping("/{seriesId}")
    public ResponseEntity<SeriesResponse> getSeries(@PathVariable Long seriesId) {
        SeriesResponse response = seriesService.getSeries(seriesId);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 내 시리즈/폴더 목록
    // ============================================
    @GetMapping("/me")
    public ResponseEntity<List<SeriesListItemDto>> getMySeries(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String type) {
        List<SeriesListItemDto> series = seriesService.getMySeries(userId, type);
        return ResponseEntity.ok(series);
    }

    // ============================================
    // 사용자의 공개 시리즈/폴더 목록
    // ============================================
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SeriesListItemDto>> getUserSeries(@PathVariable Long userId) {
        List<SeriesListItemDto> series = seriesService.getUserSeries(userId);
        return ResponseEntity.ok(series);
    }

    // ============================================
    // 시리즈/폴더 정렬 순서 변경
    // TODO: 나중에 추가하고 싶다
    // ============================================
    /*
    @PutMapping("/reorder")
    public ResponseEntity<Void> reorderSeries(
            @AuthenticationPrincipal Long userId,
            @RequestBody List<Long> seriesIds) {
        seriesService.reorderSeries(userId, seriesIds);
        return ResponseEntity.ok().build();
    }
    */
}
