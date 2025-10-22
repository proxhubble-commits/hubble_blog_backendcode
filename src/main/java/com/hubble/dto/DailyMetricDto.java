package com.hubble.dto;

import java.time.LocalDate;

public class DailyMetricDto {
    
    private LocalDate date;
    private Integer views;
    private Integer likes;
    private Integer comments;
    private Integer posts;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }
    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }
    public Integer getComments() { return comments; }
    public void setComments(Integer comments) { this.comments = comments; }
    public Integer getPosts() { return posts; }
    public void setPosts(Integer posts) { this.posts = posts; }
}
