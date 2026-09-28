package com.releasepilot.defect;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api")
public class DefectController {
    private final DefectService service; public DefectController(DefectService service) { this.service = service; }
    @GetMapping("/releases/{releaseId}/defects") public List<DefectResponse> list(@PathVariable Long releaseId) { return service.list(releaseId); }
    @PostMapping("/defects") public ResponseEntity<DefectResponse> create(@Valid @RequestBody CreateDefectRequest request) {
        DefectResponse defect = service.create(request); return ResponseEntity.created(URI.create("/api/defects/" + defect.id())).body(defect);
    }
}
