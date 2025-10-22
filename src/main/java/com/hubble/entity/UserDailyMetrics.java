package com.hubble.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_daily_metrics", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}))
public class UserDailyMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "posts_published", nullable = false)
    private Integer postsPublished = 0;

    @Column(name = "comments_created", nullable = false)
    private Integer commentsCreated = 0;

    @Column(name = "likes_received", nullable = false)
    private Integer likesReceived = 0;

    @Column(name = "total_views", nullable = false)
    private Integer totalViews = 0;

    @Column(name = "new_subscribers", nullable = false)
    private Integer newSubscribers = 0;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Integer getPostsPublished() { return postsPublished; }
    public void setPostsPublished(Integer postsPublished) { this.postsPublished = postsPublished; }
    public Integer getCommentsCreated() { return commentsCreated; }
    public void setCommentsCreated(Integer commentsCreated) { this.commentsCreated = commentsCreated; }
    public Integer getLikesReceived() { return likesReceived; }
    public void setLikesReceived(Integer likesReceived) { this.likesReceived = likesReceived; }
    public Integer getTotalViews() { return totalViews; }
    public void setTotalViews(Integer totalViews) { this.totalViews = totalViews; }
    public Integer getNewSubscribers() { return newSubscribers; }
    public void setNewSubscribers(Integer newSubscribers) { this.newSubscribers = newSubscribers; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
