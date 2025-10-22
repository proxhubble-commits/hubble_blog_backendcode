package com.hubble.service;

import com.hubble.dto.*;
import com.hubble.entity.Series;
import com.hubble.entity.User;
import com.hubble.repository.SeriesRepository;
import com.hubble.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SeriesService {
    
    private final SeriesRepository seriesRepository;
    private final UserRepository userRepository;

    public SeriesResponse createSeries(Long userId, CreateSeriesRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Series series = new Series();
        series.setUser(user);
        series.setTitle(request.getTitle());
        series.setSlug(generateSlug(request.getTitle(), userId));
        series.setType(request.getType());
        
        series = seriesRepository.save(series);
        return toSeriesResponse(series);
    }

    public SeriesResponse updateSeries(Long userId, Long seriesId, UpdateSeriesRequest request) {
        Series series = seriesRepository.findById(seriesId)
            .orElseThrow(() -> new IllegalArgumentException("Series not found"));
        
        if (!series.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        if (request.getTitle() != null) {
            series.setTitle(request.getTitle());
            series.setSlug(generateSlug(request.getTitle(), userId));
        }
        
        series = seriesRepository.save(series);
        return toSeriesResponse(series);
    }

    public void deleteSeries(Long userId, Long seriesId) {
        Series series = seriesRepository.findById(seriesId)
            .orElseThrow(() -> new IllegalArgumentException("Series not found"));
        
        if (!series.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        if (series.getPostsCount() > 0) {
            throw new IllegalArgumentException("Cannot delete series with posts");
        }
        
        seriesRepository.delete(series);
    }

    @Transactional(readOnly = true)
    public SeriesResponse getSeries(Long seriesId) {
        Series series = seriesRepository.findById(seriesId)
            .orElseThrow(() -> new IllegalArgumentException("Series not found"));
        return toSeriesResponse(series);
    }

    @Transactional(readOnly = true)
    public List<SeriesListItemDto> getMySeries(Long userId, String type) {
        List<Series> seriesList;
        
        if (type != null && !type.isEmpty()) {
            seriesList = seriesRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type.toUpperCase());
        } else {
            seriesList = seriesRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        
        return seriesList.stream()
            .map(this::toSeriesListItem)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeriesListItemDto> getUserSeries(Long userId) {
        List<Series> seriesList = seriesRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return seriesList.stream()
            .filter(series -> series.getPostsCount() > 0)
            .map(this::toSeriesListItem)
            .collect(Collectors.toList());
    }

    private String generateSlug(String title, Long userId) {
        String slug = title.toLowerCase()
            .replaceAll("[^a-z0-9가-힣]", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
        
        String finalSlug = slug;
        int counter = 1;
        while (seriesRepository.existsByUserIdAndSlug(userId, finalSlug)) {
            finalSlug = slug + "-" + counter++;
        }
        
        return finalSlug;
    }
    
    private SeriesResponse toSeriesResponse(Series series) {
        SeriesResponse response = new SeriesResponse();
        response.setId(series.getId());
        response.setUserId(series.getUser().getId());
        response.setUserNickname(series.getUser().getNickname());
        response.setTitle(series.getTitle());
        response.setSlug(series.getSlug());
        response.setType(series.getType());
        response.setPostsCount(series.getPostsCount());
        response.setCreatedAt(series.getCreatedAt());
        response.setUpdatedAt(series.getUpdatedAt());
        return response;
    }
    
    private SeriesListItemDto toSeriesListItem(Series series) {
        SeriesListItemDto dto = new SeriesListItemDto();
        dto.setId(series.getId());
        dto.setTitle(series.getTitle());
        dto.setSlug(series.getSlug());
        dto.setType(series.getType());
        dto.setPostsCount(series.getPostsCount());
        return dto;
    }
}
