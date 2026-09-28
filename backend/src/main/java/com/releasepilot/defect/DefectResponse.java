package com.releasepilot.defect;
public record DefectResponse(Long id, Long releaseId, Long testRunId, String summary, DefectSeverity severity, DefectStatus status) {
    static DefectResponse from(DefectEntity defect) { return new DefectResponse(defect.getId(), defect.getRelease().getId(), defect.getTestRun() == null ? null : defect.getTestRun().getId(), defect.getSummary(), defect.getSeverity(), defect.getStatus()); }
}
