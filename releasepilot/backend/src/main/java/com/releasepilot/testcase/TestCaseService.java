package com.releasepilot.testcase;

import com.releasepilot.requirement.RequirementEntity;
import com.releasepilot.requirement.RequirementRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service @Transactional(readOnly = true)
public class TestCaseService {
    private final TestCaseRepository repository; private final RequirementRepository requirementRepository;
    public TestCaseService(TestCaseRepository repository, RequirementRepository requirementRepository) { this.repository = repository; this.requirementRepository = requirementRepository; }
    public List<TestCaseResponse> list(Long releaseId) { return repository.findByRequirementReleaseIdOrderByIdAsc(releaseId).stream().map(TestCaseResponse::from).toList(); }
    @Transactional public TestCaseResponse create(CreateTestCaseRequest request) {
        RequirementEntity requirement = requirementRepository.findById(request.requirementId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requirement not found."));
        TestCaseEntity saved = repository.save(new TestCaseEntity(request.title().trim(), request.preconditions().trim(), request.steps().trim(), request.expectedResult().trim(), request.priority(), request.type(), requirement));
        return TestCaseResponse.from(saved);
    }
}
