package com.releasepilot.draft;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/releases/{releaseId}/test-case-drafts")
public class TestCaseDraftController {
    private final TestCaseDraftService service;

    public TestCaseDraftController(TestCaseDraftService service) {
        this.service = service;
    }

    @PostMapping
    public TestCaseDraftBatchResponse create(
            @PathVariable Long releaseId,
            @Valid @RequestBody CreateTestCaseDraftRequest request) {
        return service.generate(releaseId, request);
    }
}
