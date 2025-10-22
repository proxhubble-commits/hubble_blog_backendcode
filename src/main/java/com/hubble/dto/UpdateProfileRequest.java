package com.hubble.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Map;

public class UpdateProfileRequest {
    
    @Size(max = 1000, message = "Bio must be less than 1000 characters")
    private String bio;
    
    @Size(max = 255)
    private String website;
    
    @Size(max = 100)
    private String location;
    
    private LocalDate birthDate;
    
    @Size(max = 20)
    private String phone;
    
    private String avatarUrl;
    private String coverUrl;
    
    private Map<String, String> socialLinks;
    
    @Size(max = 100)
    private String authorName;
    
    @Size(max = 500)
    private String expertise;

    // Getters and Setters
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public Map<String, String> getSocialLinks() { return socialLinks; }
    public void setSocialLinks(Map<String, String> socialLinks) { this.socialLinks = socialLinks; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }
}
