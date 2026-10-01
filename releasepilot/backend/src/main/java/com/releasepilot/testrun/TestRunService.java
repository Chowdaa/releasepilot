package com.releasepilot.testrun;

import com.releasepilot.testcase.TestCaseEntity;
import com.releasepilot.testcase.TestCaseRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service @Transactional(readOnly = true)
public class TestRunService {
    private final TestRunRepository repository; private final TestCaseRepository testCaseRepository;
    public TestRunService(TestRunRepository repository, TestCaseRepository testCaseRepository) { this.repository = repository; this.testCaseRepository = testCaseRepository; }
    public List<TestRunResponse> listForRelease(Long releaseId) {
        return repository.findByTestCaseRequirementReleaseIdOrderByExecutedAtDesc(releaseId).stream().map(TestRunResponse::from).toList();
    }
    @Transactional public TestRunResponse create(CreateTestRunRequest request) {
        TestCaseEntity testCase = testCaseRepository.findById(request.testCaseId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test case not found."));
        return TestRunResponse.from(repository.save(new TestRunEntity(testCase, request.result(), request.executionType().trim(), request.notes())));
    }
}
