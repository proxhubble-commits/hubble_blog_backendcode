package com.hubble.dto;

import java.util.List;

public class NoteStatsResponse {
    
    private Long noteId;
    private String noteTitle;
    
    private Integer totalViews;
    private Integer totalLikes;
    private Integer totalComments;
    private Integer totalBookmarks;
    
    private List<DailyMetricDto> dailyStats;

    public Long getNoteId() { return noteId; }
    public void setNoteId(Long noteId) { this.noteId = noteId; }
    public String getNoteTitle() { return noteTitle; }
    public void setNoteTitle(String noteTitle) { this.noteTitle = noteTitle; }
    public Integer getTotalViews() { return totalViews; }
    public void setTotalViews(Integer totalViews) { this.totalViews = totalViews; }
    public Integer getTotalLikes() { return totalLikes; }
    public void setTotalLikes(Integer totalLikes) { this.totalLikes = totalLikes; }
    public Integer getTotalComments() { return totalComments; }
    public void setTotalComments(Integer totalComments) { this.totalComments = totalComments; }
    public Integer getTotalBookmarks() { return totalBookmarks; }
    public void setTotalBookmarks(Integer totalBookmarks) { this.totalBookmarks = totalBookmarks; }
    public List<DailyMetricDto> getDailyStats() { return dailyStats; }
    public void setDailyStats(List<DailyMetricDto> dailyStats) { this.dailyStats = dailyStats; }
}
