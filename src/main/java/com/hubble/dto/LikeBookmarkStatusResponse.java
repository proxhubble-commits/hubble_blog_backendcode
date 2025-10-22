package com.hubble.dto;

public class LikeBookmarkStatusResponse {
    
    private Boolean isLiked;
    private Boolean isBookmarked;
    private Integer likesCount;
    private Integer bookmarksCount;

    public Boolean getIsLiked() { return isLiked; }
    public void setIsLiked(Boolean isLiked) { this.isLiked = isLiked; }
    public Boolean getIsBookmarked() { return isBookmarked; }
    public void setIsBookmarked(Boolean isBookmarked) { this.isBookmarked = isBookmarked; }
    public Integer getLikesCount() { return likesCount; }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }
    public Integer getBookmarksCount() { return bookmarksCount; }
    public void setBookmarksCount(Integer bookmarksCount) { this.bookmarksCount = bookmarksCount; }
}
