package com.releasepilot.dashboard;

import com.releasepilot.defect.*;
import com.releasepilot.release.*;
import com.releasepilot.requirement.*;
import com.releasepilot.testcase.*;
import com.releasepilot.testrun.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service @Transactional(readOnly = true)
public class DashboardService {
    private static final List<DefectStatus> OPEN = List.of(DefectStatus.OPEN, DefectStatus.IN_PROGRESS);
    private final ReleaseRepository releases; private final RequirementRepository requirements; private final TestCaseRepository testCases; private final TestRunRepository testRuns; private final DefectRepository defects;
    public DashboardService(ReleaseRepository releases, RequirementRepository requirements, TestCaseRepository testCases, TestRunRepository testRuns, DefectRepository defects) { this.releases = releases; this.requirements = requirements; this.testCases = testCases; this.testRuns = testRuns; this.defects = defects; }
    public DashboardResponse get(Long releaseId) {
        ReleaseEntity release = releases.findById(releaseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Release not found."));
        List<RequirementEntity> requirementList = requirements.findByReleaseIdOrderByIdAsc(releaseId);
        long requirementCount = requirementList.size(); long covered = requirementList.stream().filter(r -> testCases.countByRequirementId(r.getId()) > 0).count();
        long testCaseCount = testCases.findByRequirementReleaseIdOrderByIdAsc(releaseId).size(); long runCount = testRuns.countByTestCaseRequirementReleaseId(releaseId); long passed = testRuns.countByTestCaseRequirementReleaseIdAndResult(releaseId, TestResult.PASS); long failed = testRuns.countByTestCaseRequirementReleaseIdAndResult(releaseId, TestResult.FAIL);
        long openDefects = defects.countByReleaseIdAndStatusIn(releaseId, OPEN); long blockers = defects.countByReleaseIdAndSeverityAndStatusIn(releaseId, DefectSeverity.BLOCKER, OPEN); double passRate = runCount == 0 ? 0 : Math.round((passed * 10000.0 / runCount)) / 100.0;
        List<String> reasons = new ArrayList<>(); if (blockers > 0) reasons.add("Open blocker defects: " + blockers); if (covered < requirementCount) reasons.add("Requirements without a linked test case: " + (requirementCount - covered)); if (runCount == 0) reasons.add("No test-run evidence recorded."); else if (passRate < 90) reasons.add("Test pass rate is below 90%: " + passRate + "%"); if (reasons.isEmpty()) reasons.add("All MVP release-quality rules passed.");
        return new DashboardResponse(releaseId, release.getName(), reasons.size() == 1 && reasons.get(0).startsWith("All") ? ReleaseDecision.GO : ReleaseDecision.NO_GO, reasons, requirementCount, covered, testCaseCount, runCount, passed, failed, openDefects, blockers, passRate);
    }
}
