package com.hubble.service;

import com.hubble.dto.*;
import com.hubble.entity.*;
import com.hubble.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    
    private final UserMetricsRepository userMetricsRepository;
    private final UserDailyMetricsRepository userDailyMetricsRepository;
    private final NoteDailyMetricsRepository noteDailyMetricsRepository;
    private final NoteRepository noteRepository;

    public DashboardSummaryResponse getDashboardSummary(Long userId) {
        UserMetrics metrics = userMetricsRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("User metrics not found"));
        
        DashboardSummaryResponse response = new DashboardSummaryResponse();
        
        response.setTotalPosts(metrics.getPostsCount());
        response.setPublishedPosts(metrics.getPublishedCount());
        response.setTotalViews(metrics.getTotalViews());
        response.setTotalLikes(metrics.getTotalLikes());
        response.setSubscribersCount(metrics.getSubscribersCount());
        
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(30);
        
        Long viewsLast30 = userDailyMetricsRepository.sumViewsByUserIdAndDateBetween(userId, startDate, today);
        Long likesLast30 = userDailyMetricsRepository.sumLikesByUserIdAndDateBetween(userId, startDate, today);
        
        response.setViewsLast30Days(viewsLast30);
        response.setLikesLast30Days(likesLast30);
        
        List<UserDailyMetrics> dailyMetrics = userDailyMetricsRepository
            .findByUserIdAndDateBetweenOrderByDateAsc(userId, startDate, today);
        
        List<DailyMetricDto> dailyData = dailyMetrics.stream()
            .map(m -> {
                DailyMetricDto dto = new DailyMetricDto();
                dto.setDate(m.getDate());
                dto.setViews(m.getTotalViews());
                dto.setLikes(m.getLikesReceived());
                dto.setComments(m.getCommentsCreated());
                dto.setPosts(m.getPostsPublished());
                return dto;
            })
            .collect(Collectors.toList());
        
        response.setDailyMetrics(dailyData);
        
        return response;
    }

    public NoteStatsResponse getNoteStats(Long userId, Long noteId) {
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        
        if (!note.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        NoteStatsResponse response = new NoteStatsResponse();
        response.setNoteId(note.getId());
        response.setNoteTitle(note.getTitle());
        response.setTotalViews(note.getViewsCount());
        response.setTotalLikes(note.getLikesCount());
        response.setTotalComments(note.getCommentsCount());
        response.setTotalBookmarks(note.getBookmarksCount());
        
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(30);
        
        List<NoteDailyMetrics> dailyMetrics = noteDailyMetricsRepository
            .findByNoteIdAndDateBetweenOrderByDateAsc(noteId, startDate, today);
        
        List<DailyMetricDto> dailyData = dailyMetrics.stream()
            .map(m -> {
                DailyMetricDto dto = new DailyMetricDto();
                dto.setDate(m.getDate());
                dto.setViews(m.getViewsCount());
                dto.setLikes(m.getLikesCount());
                dto.setComments(m.getCommentsCount());
                return dto;
            })
            .collect(Collectors.toList());
        
        response.setDailyStats(dailyData);
        
        return response;
    }
}
