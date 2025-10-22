package com.hubble.dto;

import java.time.OffsetDateTime;

public class PublishNoteRequest {
    
    private OffsetDateTime scheduledAt;

    public OffsetDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(OffsetDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
}
