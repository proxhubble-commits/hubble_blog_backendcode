package com.hubble.dto;

import jakarta.validation.constraints.NotNull;

public class AddInterestRequest {
    
    @NotNull(message = "Interest ID is required")
    private Long interestId;

    public Long getInterestId() { return interestId; }
    public void setInterestId(Long interestId) { this.interestId = interestId; }
}
