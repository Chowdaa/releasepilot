package com.releasepilot.testcase;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api")
public class TestCaseController {
    private final TestCaseService service;
    public TestCaseController(TestCaseService service) { this.service = service; }
    @GetMapping("/releases/{releaseId}/test-cases") public List<TestCaseResponse> list(@PathVariable Long releaseId) { return service.list(releaseId); }
    @PostMapping("/test-cases") public ResponseEntity<TestCaseResponse> create(@Valid @RequestBody CreateTestCaseRequest request) {
        TestCaseResponse testCase = service.create(request);
        return ResponseEntity.created(URI.create("/api/test-cases/" + testCase.id())).body(testCase);
    }
}
