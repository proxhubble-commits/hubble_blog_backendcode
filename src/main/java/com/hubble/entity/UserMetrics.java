package com.hubble.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_metrics")
public class UserMetrics {
    
    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "posts_count", nullable = false)
    private Integer postsCount = 0;

    @Column(name = "published_count", nullable = false)
    private Integer publishedCount = 0;

    @Column(name = "subscribers_count", nullable = false)
    private Integer subscribersCount = 0;

    @Column(name = "total_views", nullable = false)
    private Long totalViews = 0L;

    @Column(name = "total_likes", nullable = false)
    private Integer totalLikes = 0;

    @Column(name = "bookmarks_count", nullable = false)
    private Integer bookmarksCount = 0;

    @Column(name = "last_published_at")
    private OffsetDateTime lastPublishedAt;

    @Column(name = "last_active_at")
    private OffsetDateTime lastActiveAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Integer getPostsCount() { return postsCount; }
    public void setPostsCount(Integer postsCount) { this.postsCount = postsCount; }
    public Integer getPublishedCount() { return publishedCount; }
    public void setPublishedCount(Integer publishedCount) { this.publishedCount = publishedCount; }
    public Integer getSubscribersCount() { return subscribersCount; }
    public void setSubscribersCount(Integer subscribersCount) { this.subscribersCount = subscribersCount; }
    public Long getTotalViews() { return totalViews; }
    public void setTotalViews(Long totalViews) { this.totalViews = totalViews; }
    public Integer getTotalLikes() { return totalLikes; }
    public void setTotalLikes(Integer totalLikes) { this.totalLikes = totalLikes; }
    public Integer getBookmarksCount() { return bookmarksCount; }
    public void setBookmarksCount(Integer bookmarksCount) { this.bookmarksCount = bookmarksCount; }
    public OffsetDateTime getLastPublishedAt() { return lastPublishedAt; }
    public void setLastPublishedAt(OffsetDateTime lastPublishedAt) { this.lastPublishedAt = lastPublishedAt; }
    public OffsetDateTime getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(OffsetDateTime lastActiveAt) { this.lastActiveAt = lastActiveAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
