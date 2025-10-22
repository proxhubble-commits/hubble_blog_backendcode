package com.hubble.dto;

import java.util.List;

public class DashboardSummaryResponse {
    
    private Integer totalPosts;
    private Integer publishedPosts;
    private Long totalViews;
    private Integer totalLikes;
    private Integer totalComments;
    private Integer subscribersCount;
    
    private Long viewsLast30Days;
    private Long likesLast30Days;
    private Integer newSubscribersLast30Days;
    
    private List<DailyMetricDto> dailyMetrics;

    // Getters and Setters
    public Integer getTotalPosts() { return totalPosts; }
    public void setTotalPosts(Integer totalPosts) { this.totalPosts = totalPosts; }
    public Integer getPublishedPosts() { return publishedPosts; }
    public void setPublishedPosts(Integer publishedPosts) { this.publishedPosts = publishedPosts; }
    public Long getTotalViews() { return totalViews; }
    public void setTotalViews(Long totalViews) { this.totalViews = totalViews; }
    public Integer getTotalLikes() { return totalLikes; }
    public void setTotalLikes(Integer totalLikes) { this.totalLikes = totalLikes; }
    public Integer getTotalComments() { return totalComments; }
    public void setTotalComments(Integer totalComments) { this.totalComments = totalComments; }
    public Integer getSubscribersCount() { return subscribersCount; }
    public void setSubscribersCount(Integer subscribersCount) { this.subscribersCount = subscribersCount; }
    public Long getViewsLast30Days() { return viewsLast30Days; }
    public void setViewsLast30Days(Long viewsLast30Days) { this.viewsLast30Days = viewsLast30Days; }
    public Long getLikesLast30Days() { return likesLast30Days; }
    public void setLikesLast30Days(Long likesLast30Days) { this.likesLast30Days = likesLast30Days; }
    public Integer getNewSubscribersLast30Days() { return newSubscribersLast30Days; }
    public void setNewSubscribersLast30Days(Integer newSubscribersLast30Days) { this.newSubscribersLast30Days = newSubscribersLast30Days; }
    public List<DailyMetricDto> getDailyMetrics() { return dailyMetrics; }
    public void setDailyMetrics(List<DailyMetricDto> dailyMetrics) { this.dailyMetrics = dailyMetrics; }
}
