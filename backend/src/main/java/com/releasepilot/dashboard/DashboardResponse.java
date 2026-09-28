package com.releasepilot.dashboard;

import java.util.List;
public record DashboardResponse(Long releaseId, String releaseName, ReleaseDecision decision, List<String> reasons,
                                long requirements, long requirementsWithTests, long testCases, long testRuns,
                                long passedRuns, long failedRuns, long openDefects, long openBlockers, double passRate) {}
