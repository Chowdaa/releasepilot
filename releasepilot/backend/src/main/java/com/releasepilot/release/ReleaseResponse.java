package com.releasepilot.release;

import java.time.LocalDate;

public record ReleaseResponse(Long id, String name, LocalDate targetDate, ReleaseStatus status) {
    static ReleaseResponse from(ReleaseEntity release) {
        return new ReleaseResponse(release.getId(), release.getName(), release.getTargetDate(), release.getStatus());
    }
}
