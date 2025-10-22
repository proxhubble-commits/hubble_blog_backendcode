package com.hubble.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

public class UpdateNoteRequest {
    
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;
    
    private String content;
    private String summary;
    private Long seriesId;
    private Integer seriesOrder;
    private String thumbnailUrl;
    private String visibility;
    private List<String> tags;
    
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public Long getSeriesId() { return seriesId; }
    public void setSeriesId(Long seriesId) { this.seriesId = seriesId; }
    public Integer getSeriesOrder() { return seriesOrder; }
    public void setSeriesOrder(Integer seriesOrder) { this.seriesOrder = seriesOrder; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public String getMetaTitle() { return metaTitle; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }
    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }
    public String getMetaKeywords() { return metaKeywords; }
    public void setMetaKeywords(String metaKeywords) { this.metaKeywords = metaKeywords; }
}
