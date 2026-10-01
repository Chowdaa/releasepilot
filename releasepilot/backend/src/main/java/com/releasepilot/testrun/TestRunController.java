package com.releasepilot.testrun;

import jakarta.validation.Valid;
import java.util.List;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api")
public class TestRunController {
    private final TestRunService service; public TestRunController(TestRunService service) { this.service = service; }
    @GetMapping("/releases/{releaseId}/test-runs") public List<TestRunResponse> list(@PathVariable Long releaseId) {
        return service.listForRelease(releaseId);
    }
    @PostMapping("/test-runs") public ResponseEntity<TestRunResponse> create(@Valid @RequestBody CreateTestRunRequest request) {
        TestRunResponse run = service.create(request); return ResponseEntity.created(URI.create("/api/test-runs/" + run.id())).body(run);
    }
}
