package com.hubble.dto;

import java.util.List;
import java.util.Map;

public class ProfileResponse {
    
    private Long userId;
    private String email;
    private String nickname;
    
    private String bio;
    private String website;
    private String location;
    private String avatarUrl;
    private String coverUrl;
    private Map<String, String> socialLinks;
    private String authorName;
    private String expertise;
    
    private Integer postsCount;
    private Integer publishedCount;
    private Integer subscribersCount;
    private Long totalViews;
    private Integer totalLikes;
    
    private List<UserSkillDto> skills;
    private List<InterestDto> interests;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
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
    public Integer getPostsCount() { return postsCount; }
    public void setPostsCount(Integer postsCount) { this.postsCount = postsCount; }
    public Integer getPublishedCount() { return publishedCount; }
    public void setPublishedCount(Integer publishedCount) { this.publishedCount = publishedCount; }
    public Integer getSubscribersCount() { return subscribersCount; }
    public void setSubscribersCount(Integer subscribersCount) { this.subscribersCount = subscribersCount; }
    public Long getTotalViews() { return totalViews; }
    public void setTotalViews(Long totalViews) { this.totalViews = totalViews; }
    public Integer getTotalLikes() { return totalLikes; }
    public void setTotalLikes(Integer totalLikes) { this.totalLikes = totalLikes; }
    public List<UserSkillDto> getSkills() { return skills; }
    public void setSkills(List<UserSkillDto> skills) { this.skills = skills; }
    public List<InterestDto> getInterests() { return interests; }
    public void setInterests(List<InterestDto> interests) { this.interests = interests; }
}
