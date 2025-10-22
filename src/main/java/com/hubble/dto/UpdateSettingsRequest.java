package com.hubble.dto;

public class UpdateSettingsRequest {
    
    private String profileVisibility;
    private Boolean showEmail;
    private Boolean showStats;
    private Boolean allowComments;
    private String defaultEditor;
    private Boolean autoSave;
    private String theme;

    public String getProfileVisibility() { return profileVisibility; }
    public void setProfileVisibility(String profileVisibility) { this.profileVisibility = profileVisibility; }
    public Boolean getShowEmail() { return showEmail; }
    public void setShowEmail(Boolean showEmail) { this.showEmail = showEmail; }
    public Boolean getShowStats() { return showStats; }
    public void setShowStats(Boolean showStats) { this.showStats = showStats; }
    public Boolean getAllowComments() { return allowComments; }
    public void setAllowComments(Boolean allowComments) { this.allowComments = allowComments; }
    public String getDefaultEditor() { return defaultEditor; }
    public void setDefaultEditor(String defaultEditor) { this.defaultEditor = defaultEditor; }
    public Boolean getAutoSave() { return autoSave; }
    public void setAutoSave(Boolean autoSave) { this.autoSave = autoSave; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
}
