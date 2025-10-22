package com.hubble.dto;

import jakarta.validation.constraints.Size;

public class UpdateSeriesRequest {
    
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
