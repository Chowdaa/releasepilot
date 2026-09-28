package com.releasepilot.testrun;

import org.springframework.data.jpa.repository.JpaRepository;
public interface TestRunRepository extends JpaRepository<TestRunEntity, Long> {
    long countByTestCaseRequirementReleaseId(Long releaseId);
    long countByTestCaseRequirementReleaseIdAndResult(Long releaseId, TestResult result);
}
