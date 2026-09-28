package com.releasepilot.testcase;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Long> {
    List<TestCaseEntity> findByRequirementReleaseIdOrderByIdAsc(Long releaseId);
    long countByRequirementId(Long requirementId);
}
