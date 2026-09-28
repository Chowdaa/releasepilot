package com.releasepilot.requirement;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/releases/{releaseId}/requirements")
public class RequirementController {
    private final RequirementService requirementService;

    public RequirementController(RequirementService requirementService) {
        this.requirementService = requirementService;
    }

    @GetMapping
    public List<RequirementResponse> list(@PathVariable Long releaseId) {
        return requirementService.listForRelease(releaseId);
    }

    @PostMapping
    public ResponseEntity<RequirementResponse> create(
            @PathVariable Long releaseId,
            @Valid @RequestBody CreateRequirementRequest request) {
        RequirementResponse requirement = requirementService.create(releaseId, request);
        return ResponseEntity.created(URI.create("/api/requirements/" + requirement.id())).body(requirement);
    }
}
