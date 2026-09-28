package com.releasepilot.defect;

import jakarta.validation.constraints.*;
public record CreateDefectRequest(@NotNull Long releaseId, Long testRunId, @NotBlank @Size(max = 240) String summary, @NotNull DefectSeverity severity) {}
