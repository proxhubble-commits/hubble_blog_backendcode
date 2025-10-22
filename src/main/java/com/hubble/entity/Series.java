package com.hubble.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "series", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "slug"}))
public class Series {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String slug;

    @Column(nullable = false, length = 20)
    private String type = "FOLDER";

    @Column(name = "posts_count", nullable = false)
    private Integer postsCount = 0;

    // @Column(name = "sort_order", nullable = false)
    // private Integer sortOrder = 0; // TODO: 나중에 추가하고 싶다 (정렬 순서)
    
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getPostsCount() { return postsCount; }
    public void setPostsCount(Integer postsCount) { this.postsCount = postsCount; }
    // public Integer getSortOrder() { return sortOrder; }
    // public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
