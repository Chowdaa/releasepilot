package com.releasepilot.testrun;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/test-runs")
public class TestRunController {
    private final TestRunService service; public TestRunController(TestRunService service) { this.service = service; }
    @PostMapping public ResponseEntity<TestRunResponse> create(@Valid @RequestBody CreateTestRunRequest request) {
        TestRunResponse run = service.create(request); return ResponseEntity.created(URI.create("/api/test-runs/" + run.id())).body(run);
    }
}
