package com.hubble.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_settings")
public class UserSettings {
    
    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "profile_visibility", nullable = false, length = 20)
    private String profileVisibility = "PUBLIC";

    @Column(name = "show_email", nullable = false)
    private Boolean showEmail = false;

    @Column(name = "show_stats", nullable = false)
    private Boolean showStats = true;

    @Column(name = "allow_comments", nullable = false)
    private Boolean allowComments = true;

    @Type(JsonBinaryType.class)
    @Column(name = "email_notifications", columnDefinition = "jsonb", nullable = false)
    private String emailNotifications = "{\"comments\": true, \"likes\": true, \"subscribers\": true}";

    @Type(JsonBinaryType.class)
    @Column(name = "push_notifications", columnDefinition = "jsonb", nullable = false)
    private String pushNotifications = "{\"comments\": true, \"likes\": true, \"subscribers\": true}";

    @Column(name = "default_editor", nullable = false, length = 20)
    private String defaultEditor = "MARKDOWN";

    @Column(name = "auto_save", nullable = false)
    private Boolean autoSave = true;

    @Column(nullable = false, length = 10)
    private String language = "ko";

    @Column(nullable = false, length = 50)
    private String timezone = "Asia/Seoul";

    @Column(nullable = false, length = 20)
    private String theme = "light";

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getProfileVisibility() { return profileVisibility; }
    public void setProfileVisibility(String profileVisibility) { this.profileVisibility = profileVisibility; }
    public Boolean getShowEmail() { return showEmail; }
    public void setShowEmail(Boolean showEmail) { this.showEmail = showEmail; }
    public Boolean getShowStats() { return showStats; }
    public void setShowStats(Boolean showStats) { this.showStats = showStats; }
    public Boolean getAllowComments() { return allowComments; }
    public void setAllowComments(Boolean allowComments) { this.allowComments = allowComments; }
    public String getEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(String emailNotifications) { this.emailNotifications = emailNotifications; }
    public String getPushNotifications() { return pushNotifications; }
    public void setPushNotifications(String pushNotifications) { this.pushNotifications = pushNotifications; }
    public String getDefaultEditor() { return defaultEditor; }
    public void setDefaultEditor(String defaultEditor) { this.defaultEditor = defaultEditor; }
    public Boolean getAutoSave() { return autoSave; }
    public void setAutoSave(Boolean autoSave) { this.autoSave = autoSave; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
