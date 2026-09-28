package com.releasepilot.draft;

import com.releasepilot.requirement.RequirementPriority;
import com.releasepilot.testcase.TestCaseType;

public record TestCaseDraftResponse(
        String title,
        String preconditions,
        String steps,
        String expectedResult,
        RequirementPriority priority,
        TestCaseType type
) {}
