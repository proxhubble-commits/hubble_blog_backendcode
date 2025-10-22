package com.hubble.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCommentRequest {
    
    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must be less than 5000 characters")
    private String content;
    
    private Long parentId;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}
