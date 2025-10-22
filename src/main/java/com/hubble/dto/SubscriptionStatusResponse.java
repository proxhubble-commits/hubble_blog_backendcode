package com.hubble.dto;

public class SubscriptionStatusResponse {
    
    private Boolean isSubscribed;
    private Boolean notificationEnabled;

    public Boolean getIsSubscribed() { return isSubscribed; }
    public void setIsSubscribed(Boolean isSubscribed) { this.isSubscribed = isSubscribed; }
    public Boolean getNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(Boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }
}
