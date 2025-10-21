package com.hubble.controller;

import com.hubble.dto.NoteListItemDto;
import com.hubble.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    
    private final SearchService searchService;

    // ============================================
    // 통합 검색 (제목 + 내용 + 요약)
    // ============================================
    @GetMapping
    public ResponseEntity<Page<NoteListItemDto>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<NoteListItemDto> results = searchService.searchNotes(q, pageable);
        return ResponseEntity.ok(results);
    }

    // ============================================
    // 태그로 검색
    // ============================================
    @GetMapping("/tag/{tagName}")
    public ResponseEntity<Page<NoteListItemDto>> searchByTag(
            @PathVariable String tagName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<NoteListItemDto> results = searchService.searchByTag(tagName, pageable);
        return ResponseEntity.ok(results);
    }

    // ============================================
    // 전체 재색인 (관리자용)
    // ============================================
    @PostMapping("/reindex")
    public ResponseEntity<Void> reindex() {
        searchService.reindexAllNotes();
        return ResponseEntity.ok().build();
    }
}
