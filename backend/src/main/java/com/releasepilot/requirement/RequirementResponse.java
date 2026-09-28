package com.releasepilot.requirement;

public record RequirementResponse(
        Long id,
        Long releaseId,
        String title,
        String description,
        RequirementPriority priority
) {
    static RequirementResponse from(RequirementEntity requirement) {
        return new RequirementResponse(
                requirement.getId(),
                requirement.getRelease().getId(),
                requirement.getTitle(),
                requirement.getDescription(),
                requirement.getPriority());
    }
}
