package com.releasepilot.testrun;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TestRunRepository extends JpaRepository<TestRunEntity, Long> {
    long countByTestCaseRequirementReleaseId(Long releaseId);
    long countByTestCaseRequirementReleaseIdAndResult(Long releaseId, TestResult result);
    List<TestRunEntity> findByTestCaseRequirementReleaseIdOrderByExecutedAtDesc(Long releaseId);
}
