package com.hubble.dto;

import java.time.OffsetDateTime;

public class SubscriptionResponse {
    
    private Long id;
    private Long authorId;
    private String authorNickname;
    private String authorAvatarUrl;
    private Boolean notificationEnabled;
    private OffsetDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getAuthorNickname() { return authorNickname; }
    public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }
    public String getAuthorAvatarUrl() { return authorAvatarUrl; }
    public void setAuthorAvatarUrl(String authorAvatarUrl) { this.authorAvatarUrl = authorAvatarUrl; }
    public Boolean getNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(Boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
