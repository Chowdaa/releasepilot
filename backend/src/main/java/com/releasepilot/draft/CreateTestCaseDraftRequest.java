package com.releasepilot.draft;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTestCaseDraftRequest(
        @NotNull Long requirementId,
        @NotBlank @Size(max = 2_000) String acceptanceCriterion
) {}
