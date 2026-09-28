package com.releasepilot.testcase;

import com.releasepilot.requirement.RequirementPriority;
import jakarta.validation.constraints.*;

public record CreateTestCaseRequest(
        @NotNull Long requirementId,
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 1000) String preconditions,
        @NotBlank @Size(max = 4000) String steps,
        @NotBlank @Size(max = 2000) String expectedResult,
        @NotNull RequirementPriority priority,
        @NotNull TestCaseType type
) {}
