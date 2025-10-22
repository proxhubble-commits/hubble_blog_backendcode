package com.hubble.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "note_daily_metrics", uniqueConstraints = @UniqueConstraint(columnNames = {"note_id", "date"}))
public class NoteDailyMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "views_count", nullable = false)
    private Integer viewsCount = 0;

    @Column(name = "unique_views", nullable = false)
    private Integer uniqueViews = 0;

    @Column(name = "likes_count", nullable = false)
    private Integer likesCount = 0;

    @Column(name = "comments_count", nullable = false)
    private Integer commentsCount = 0;

    @Column(name = "bookmarks_count", nullable = false)
    private Integer bookmarksCount = 0;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Note getNote() { return note; }
    public void setNote(Note note) { this.note = note; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Integer getViewsCount() { return viewsCount; }
    public void setViewsCount(Integer viewsCount) { this.viewsCount = viewsCount; }
    public Integer getUniqueViews() { return uniqueViews; }
    public void setUniqueViews(Integer uniqueViews) { this.uniqueViews = uniqueViews; }
    public Integer getLikesCount() { return likesCount; }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }
    public Integer getCommentsCount() { return commentsCount; }
    public void setCommentsCount(Integer commentsCount) { this.commentsCount = commentsCount; }
    public Integer getBookmarksCount() { return bookmarksCount; }
    public void setBookmarksCount(Integer bookmarksCount) { this.bookmarksCount = bookmarksCount; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
