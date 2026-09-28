package com.releasepilot.dashboard;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/releases/{releaseId}/dashboard")
public class DashboardController { private final DashboardService service; public DashboardController(DashboardService service) { this.service = service; } @GetMapping public DashboardResponse get(@PathVariable Long releaseId) { return service.get(releaseId); } }
