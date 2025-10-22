package com.hubble.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubble.dto.*;
import com.hubble.entity.*;
import com.hubble.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final UserMetricsRepository userMetricsRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillAliasRepository skillAliasRepository;
    private final InterestRepository interestRepository;
    private final UserInterestRepository userInterestRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        UserMetrics metrics = userMetricsRepository.findByUserId(userId).orElse(null);
        
        ProfileResponse response = new ProfileResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setNickname(user.getNickname());
        
        if (profile != null) {
            response.setBio(profile.getBio());
            response.setWebsite(profile.getWebsite());
            response.setLocation(profile.getLocation());
            response.setAvatarUrl(profile.getAvatarUrl());
            response.setCoverUrl(profile.getCoverUrl());
            response.setAuthorName(profile.getAuthorName());
            response.setExpertise(profile.getExpertise());
            
            try {
                Map<String, String> links = objectMapper.readValue(profile.getSocialLinks(), Map.class);
                response.setSocialLinks(links);
            } catch (Exception e) {
                response.setSocialLinks(Map.of());
            }
        }
        
        if (metrics != null) {
            response.setPostsCount(metrics.getPostsCount());
            response.setPublishedCount(metrics.getPublishedCount());
            response.setSubscribersCount(metrics.getSubscribersCount());
            response.setTotalViews(metrics.getTotalViews());
            response.setTotalLikes(metrics.getTotalLikes());
        }
        
        List<UserSkillDto> skills = userSkillRepository.findByUserIdOrderByIsPrimaryDescCreatedAtAsc(userId)
            .stream().map(this::toUserSkillDto).collect(Collectors.toList());
        response.setSkills(skills);
        
        List<InterestDto> interests = userInterestRepository.findByUserIdOrderByCreatedAtAsc(userId)
            .stream().map(ui -> toInterestDto(ui.getInterest())).collect(Collectors.toList());
        response.setInterests(interests);
        
        return response;
    }

    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        UserProfile profile = userProfileRepository.findByUserId(userId)
            .orElseGet(() -> {
                UserProfile newProfile = new UserProfile();
                newProfile.setUser(user);
                newProfile.setUserId(userId);
                return newProfile;
            });
        
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getWebsite() != null) profile.setWebsite(request.getWebsite());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getBirthDate() != null) profile.setBirthDate(request.getBirthDate());
        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getCoverUrl() != null) profile.setCoverUrl(request.getCoverUrl());
        if (request.getAuthorName() != null) profile.setAuthorName(request.getAuthorName());
        if (request.getExpertise() != null) profile.setExpertise(request.getExpertise());
        
        if (request.getSocialLinks() != null) {
            try {
                String jsonLinks = objectMapper.writeValueAsString(request.getSocialLinks());
                profile.setSocialLinks(jsonLinks);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid social links format");
            }
        }
        
        userProfileRepository.save(profile);
        return getProfile(userId);
    }

    public UserSkillDto addSkill(Long userId, AddSkillRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Skill skill = skillRepository.findByName(request.getSkillName())
            .or(() -> {
                List<Skill> matches = skillRepository.searchByKeyword(request.getSkillName());
                return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
            })
            .orElseGet(() -> {
                Skill newSkill = new Skill();
                newSkill.setName(request.getSkillName());
                newSkill.setSlug(generateSlug(request.getSkillName()));
                newSkill.setCategory("Custom");
                return skillRepository.save(newSkill);
            });
        
        if (userSkillRepository.existsByUserIdAndSkillId(userId, skill.getId())) {
            throw new IllegalArgumentException("Skill already added");
        }
        
        UserSkill userSkill = new UserSkill();
        userSkill.setUser(user);
        userSkill.setSkill(skill);
        userSkill.setProficiency(request.getProficiency());
        userSkill.setYearsExperience(request.getYearsExperience());
        userSkill.setIsPrimary(request.getIsPrimary());
        
        userSkill = userSkillRepository.save(userSkill);
        return toUserSkillDto(userSkill);
    }

    public void removeSkill(Long userId, Long skillId) {
        if (!userSkillRepository.existsByUserIdAndSkillId(userId, skillId)) {
            throw new IllegalArgumentException("Skill not found");
        }
        userSkillRepository.deleteByUserIdAndSkillId(userId, skillId);
    }

    public InterestDto addInterest(Long userId, AddInterestRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        long currentCount = userInterestRepository.countByUserId(userId);
        if (currentCount >= 2) {
            throw new IllegalArgumentException("Maximum 2 interests allowed");
        }
        
        Interest interest = interestRepository.findById(request.getInterestId())
            .orElseThrow(() -> new IllegalArgumentException("Interest not found"));
        
        if (userInterestRepository.existsByUserIdAndInterestId(userId, interest.getId())) {
            throw new IllegalArgumentException("Interest already added");
        }
        
        UserInterest userInterest = new UserInterest();
        userInterest.setUser(user);
        userInterest.setInterest(interest);
        
        userInterestRepository.save(userInterest);
        return toInterestDto(interest);
    }

    public void removeInterest(Long userId, Long interestId) {
        if (!userInterestRepository.existsByUserIdAndInterestId(userId, interestId)) {
            throw new IllegalArgumentException("Interest not found");
        }
        userInterestRepository.deleteByUserIdAndInterestId(userId, interestId);
    }

    @Transactional(readOnly = true)
    public List<InterestDto> getAllInterests() {
        return interestRepository.findAllByOrderByIdAsc()
            .stream().map(this::toInterestDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SettingsResponse getSettings(Long userId) {
        UserSettings settings = userSettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Settings not found"));
        
        SettingsResponse response = new SettingsResponse();
        response.setProfileVisibility(settings.getProfileVisibility());
        response.setShowEmail(settings.getShowEmail());
        response.setShowStats(settings.getShowStats());
        response.setAllowComments(settings.getAllowComments());
        response.setDefaultEditor(settings.getDefaultEditor());
        response.setAutoSave(settings.getAutoSave());
        response.setLanguage(settings.getLanguage());
        response.setTimezone(settings.getTimezone());
        response.setTheme(settings.getTheme());
        
        return response;
    }

    public SettingsResponse updateSettings(Long userId, UpdateSettingsRequest request) {
        UserSettings settings = userSettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Settings not found"));
        
        if (request.getProfileVisibility() != null) settings.setProfileVisibility(request.getProfileVisibility());
        if (request.getShowEmail() != null) settings.setShowEmail(request.getShowEmail());
        if (request.getShowStats() != null) settings.setShowStats(request.getShowStats());
        if (request.getAllowComments() != null) settings.setAllowComments(request.getAllowComments());
        if (request.getDefaultEditor() != null) settings.setDefaultEditor(request.getDefaultEditor());
        if (request.getAutoSave() != null) settings.setAutoSave(request.getAutoSave());
        if (request.getTheme() != null) settings.setTheme(request.getTheme());
        
        userSettingsRepository.save(settings);
        return getSettings(userId);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
            .replaceAll("[^a-z0-9가-힣]", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
    }
    
    private UserSkillDto toUserSkillDto(UserSkill userSkill) {
        UserSkillDto dto = new UserSkillDto();
        dto.setId(userSkill.getId());
        dto.setSkillId(userSkill.getSkill().getId());
        dto.setSkillName(userSkill.getSkill().getName());
        dto.setProficiency(userSkill.getProficiency());
        dto.setYearsExperience(userSkill.getYearsExperience());
        dto.setIsPrimary(userSkill.getIsPrimary());
        return dto;
    }
    
    private InterestDto toInterestDto(Interest interest) {
        InterestDto dto = new InterestDto();
        dto.setId(interest.getId());
        dto.setName(interest.getName());
        dto.setSlug(interest.getSlug());
        dto.setIcon(interest.getIcon());
        return dto;
    }
}
