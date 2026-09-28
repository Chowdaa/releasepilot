package com.releasepilot.testcase;

import com.releasepilot.requirement.RequirementPriority;

public record TestCaseResponse(Long id, Long requirementId, String requirementTitle, String title, String preconditions,
                               String steps, String expectedResult, RequirementPriority priority, TestCaseType type) {
    static TestCaseResponse from(TestCaseEntity testCase) {
        return new TestCaseResponse(testCase.getId(), testCase.getRequirement().getId(), testCase.getRequirement().getTitle(),
                testCase.getTitle(), testCase.getPreconditions(), testCase.getSteps(), testCase.getExpectedResult(),
                testCase.getPriority(), testCase.getType());
    }
}
