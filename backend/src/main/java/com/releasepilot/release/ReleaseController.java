package com.releasepilot.release;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/releases")
public class ReleaseController {
    private final ReleaseService releaseService;

    public ReleaseController(ReleaseService releaseService) {
        this.releaseService = releaseService;
    }

    @GetMapping
    public List<ReleaseResponse> list() {
        return releaseService.list();
    }

    @PostMapping
    public ResponseEntity<ReleaseResponse> create(@Valid @RequestBody CreateReleaseRequest request) {
        ReleaseResponse release = releaseService.create(request);
        return ResponseEntity.created(URI.create("/api/releases/" + release.id())).body(release);
    }
}
