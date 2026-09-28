package com.releasepilot.testrun;

import java.time.Instant;
public record TestRunResponse(Long id, Long testCaseId, String testCaseTitle, TestResult result, String executionType, Instant executedAt, String notes) {
    static TestRunResponse from(TestRunEntity run) { return new TestRunResponse(run.getId(), run.getTestCase().getId(), run.getTestCase().getTitle(), run.getResult(), run.getExecutionType(), run.getExecutedAt(), run.getNotes()); }
}
