package com.edusmart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "badges")
public class Badge extends BaseEntity {

    @NotBlank(message = "Badge name cannot be blank")
    @Size(min = 3, max = 50, message = "Badge name must be between 3 and 50 characters")
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank(message = "Badge description cannot be blank")
    @Column(nullable = false)
    private String description;

    @NotBlank(message = "Icon URL cannot be blank")
    @Column(name = "icon_url", nullable = false)
    private String iconUrl;

    @Column(name = "requirement_points", nullable = false)
    private int requirementPoints;

    public Badge() {}

    public Badge(String name, String description, String iconUrl, int requirementPoints) {
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
        this.requirementPoints = requirementPoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getRequirementPoints() {
        return requirementPoints;
    }

    public void setRequirementPoints(int requirementPoints) {
        this.requirementPoints = requirementPoints;
    }
}
