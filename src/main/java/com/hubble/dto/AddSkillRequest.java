package com.hubble.dto;

import jakarta.validation.constraints.NotBlank;

public class AddSkillRequest {
    
    @NotBlank(message = "Skill name is required")
    private String skillName;
    
    private String proficiency = "BEGINNER";
    private Integer yearsExperience;
    private Boolean isPrimary = false;

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getProficiency() { return proficiency; }
    public void setProficiency(String proficiency) { this.proficiency = proficiency; }
    public Integer getYearsExperience() { return yearsExperience; }
    public void setYearsExperience(Integer yearsExperience) { this.yearsExperience = yearsExperience; }
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
}
