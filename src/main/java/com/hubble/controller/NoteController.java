package com.hubble.controller;

import com.hubble.dto.*;
import com.hubble.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {
    
    private final NoteService noteService;

    // ============================================
    // 노트 생성 (초안)
    // ============================================
    @PostMapping
    public ResponseEntity<NoteResponse> createNote(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateNoteRequest request) {
        NoteResponse response = noteService.createNote(userId, request);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 노트 수정
    // ============================================
    @PutMapping("/{noteId}")
    public ResponseEntity<NoteResponse> updateNote(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long noteId,
            @Valid @RequestBody UpdateNoteRequest request) {
        NoteResponse response = noteService.updateNote(userId, noteId, request);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 노트 발행
    // ============================================
    @PostMapping("/{noteId}/publish")
    public ResponseEntity<NoteResponse> publishNote(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long noteId,
            @RequestBody PublishNoteRequest request) {
        NoteResponse response = noteService.publishNote(userId, noteId, request);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 노트 삭제
    // ============================================
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long noteId) {
        noteService.deleteNote(userId, noteId);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // 노트 조회 (단건)
    // ============================================
    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponse> getNote(
            @PathVariable Long noteId,
            @AuthenticationPrincipal(errorOnInvalidType = false) Long userId) {
        NoteResponse response = noteService.getNote(noteId, userId);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 내 노트 목록
    // ============================================
    @GetMapping("/me")
    public ResponseEntity<Page<NoteListItemDto>> getMyNotes(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "DRAFT") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NoteListItemDto> notes = noteService.getMyNotes(userId, status, pageable);
        return ResponseEntity.ok(notes);
    }

    // ============================================
    // 공개 노트 목록 (피드)
    // ============================================
    @GetMapping("/public")
    public ResponseEntity<Page<NoteListItemDto>> getPublicNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<NoteListItemDto> notes = noteService.getPublicNotes(pageable);
        return ResponseEntity.ok(notes);
    }

    // ============================================
    // 노트 검색
    // ============================================
    @GetMapping("/search")
    public ResponseEntity<Page<NoteListItemDto>> searchNotes(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<NoteListItemDto> notes = noteService.searchNotes(q, pageable);
        return ResponseEntity.ok(notes);
    }

    // ============================================
    // 사용자별 공개 노트 목록
    // ============================================
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NoteListItemDto>> getUserNotes(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<NoteListItemDto> notes = noteService.getMyNotes(userId, "PUBLISHED", pageable);
        return ResponseEntity.ok(notes);
    }
}
