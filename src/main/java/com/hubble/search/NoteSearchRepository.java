package com.hubble.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface NoteSearchRepository extends ElasticsearchRepository<NoteDocument, Long> {
    
    // 제목 + 내용 통합 검색 (한글 형태소 분석)
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^3\", \"content\", \"summary^2\"], \"type\": \"best_fields\"}}], \"filter\": [{\"term\": {\"status\": \"PUBLISHED\"}}, {\"term\": {\"visibility\": \"PUBLIC\"}}]}}")
    Page<NoteDocument> searchByKeyword(String keyword, Pageable pageable);
    
    // 태그로 검색
    @Query("{\"bool\": {\"must\": [{\"term\": {\"tags\": \"?0\"}}], \"filter\": [{\"term\": {\"status\": \"PUBLISHED\"}}, {\"term\": {\"visibility\": \"PUBLIC\"}}]}}")
    Page<NoteDocument> searchByTag(String tag, Pageable pageable);
    
    // 시리즈로 검색
    Page<NoteDocument> findBySeriesIdAndStatusAndVisibility(Long seriesId, String status, String visibility, Pageable pageable);
    
    // 공개 노트만 조회
    Page<NoteDocument> findByStatusAndVisibilityOrderByPublishedAtDesc(String status, String visibility, Pageable pageable);
}
