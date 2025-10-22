package com.hubble.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class CommentResponse {
    
    private Long id;
    private Long noteId;
    private Long parentId;
    
    private Long userId;
    private String userNickname;
    private String userAvatarUrl;
    
    private String content;
    private Integer likesCount;
    private Boolean isDeleted;
    private Boolean isAuthorReply;
    private Boolean isLikedByMe;
    
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    
    private List<CommentResponse> replies;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getNoteId() { return noteId; }
    public void setNoteId(Long noteId) { this.noteId = noteId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
    public String getUserAvatarUrl() { return userAvatarUrl; }
    public void setUserAvatarUrl(String userAvatarUrl) { this.userAvatarUrl = userAvatarUrl; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getLikesCount() { return likesCount; }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    public Boolean getIsAuthorReply() { return isAuthorReply; }
    public void setIsAuthorReply(Boolean isAuthorReply) { this.isAuthorReply = isAuthorReply; }
    public Boolean getIsLikedByMe() { return isLikedByMe; }
    public void setIsLikedByMe(Boolean isLikedByMe) { this.isLikedByMe = isLikedByMe; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<CommentResponse> getReplies() { return replies; }
    public void setReplies(List<CommentResponse> replies) { this.replies = replies; }
}
