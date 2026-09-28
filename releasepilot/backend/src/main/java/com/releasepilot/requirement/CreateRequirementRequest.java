package com.releasepilot.requirement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRequirementRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 2_000) String description,
        @NotNull RequirementPriority priority
) {}
