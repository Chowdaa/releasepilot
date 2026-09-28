package com.releasepilot.testrun;

import jakarta.validation.constraints.*;
public record CreateTestRunRequest(@NotNull Long testCaseId, @NotNull TestResult result,
                                   @NotBlank @Size(max = 40) String executionType, @Size(max = 2000) String notes) {}
