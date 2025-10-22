package com.hubble.dto;

import java.time.OffsetDateTime;

public class SubscriberResponse {
    
    private Long id;
    private Long subscriberId;
    private String subscriberNickname;
    private String subscriberAvatarUrl;
    private OffsetDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSubscriberId() { return subscriberId; }
    public void setSubscriberId(Long subscriberId) { this.subscriberId = subscriberId; }
    public String getSubscriberNickname() { return subscriberNickname; }
    public void setSubscriberNickname(String subscriberNickname) { this.subscriberNickname = subscriberNickname; }
    public String getSubscriberAvatarUrl() { return subscriberAvatarUrl; }
    public void setSubscriberAvatarUrl(String subscriberAvatarUrl) { this.subscriberAvatarUrl = subscriberAvatarUrl; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
