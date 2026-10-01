package com.releasepilot.dashboard;

import static org.assertj.core.api.Assertions.assertThat;

import com.releasepilot.defect.DefectEntity;
import com.releasepilot.defect.DefectRepository;
import com.releasepilot.defect.DefectSeverity;
import com.releasepilot.release.ReleaseEntity;
import com.releasepilot.release.ReleaseRepository;
import com.releasepilot.release.ReleaseStatus;
import com.releasepilot.requirement.RequirementEntity;
import com.releasepilot.requirement.RequirementPriority;
import com.releasepilot.requirement.RequirementRepository;
import com.releasepilot.testcase.TestCaseEntity;
import com.releasepilot.testcase.TestCaseRepository;
import com.releasepilot.testcase.TestCaseType;
import com.releasepilot.testrun.TestResult;
import com.releasepilot.testrun.TestRunEntity;
import com.releasepilot.testrun.TestRunRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReleaseDecisionWorkflowTests {
    @Autowired private DashboardService dashboardService;
    @Autowired private ReleaseRepository releases;
    @Autowired private RequirementRepository requirements;
    @Autowired private TestCaseRepository testCases;
    @Autowired private TestRunRepository testRuns;
    @Autowired private DefectRepository defects;

    @Test
    void returnsGoWhenEveryRequirementHasPassingEvidenceAndNoOpenBlocker() {
        ReleaseEntity release = release();
        RequirementEntity requirement = requirement(release);
        TestCaseEntity testCase = testCases.save(new TestCaseEntity(
                "Valid checkout succeeds", "A customer and cart exist", "Submit valid payment", "Order is confirmed",
                RequirementPriority.HIGH, TestCaseType.UI, requirement));
        testRuns.save(new TestRunEntity(testCase, TestResult.PASS, "UI", "Checkout completed."));

        DashboardResponse dashboard = dashboardService.get(release.getId());

        assertThat(dashboard.decision()).isEqualTo(ReleaseDecision.GO);
        assertThat(dashboard.passRate()).isEqualTo(100.0);
        assertThat(dashboard.reasons()).containsExactly("All MVP release-quality rules passed.");
    }

    @Test
    void returnsNoGoWhenARequirementHasNoLinkedTestCase() {
        ReleaseEntity release = release();
        requirement(release);

        DashboardResponse dashboard = dashboardService.get(release.getId());

        assertThat(dashboard.decision()).isEqualTo(ReleaseDecision.NO_GO);
        assertThat(dashboard.reasons()).anyMatch(reason -> reason.startsWith("Requirements without a linked test case: 1"));
        assertThat(dashboard.reasons()).contains("No test-run evidence recorded.");
    }

    @Test
    void returnsNoGoWhenAnOpenBlockerIsLogged() {
        ReleaseEntity release = release();
        RequirementEntity requirement = requirement(release);
        TestCaseEntity testCase = testCases.save(new TestCaseEntity(
                "Payment API succeeds", "A cart exists", "Call payment endpoint", "Payment is approved",
                RequirementPriority.CRITICAL, TestCaseType.API, requirement));
        TestRunEntity run = testRuns.save(new TestRunEntity(testCase, TestResult.PASS, "API", "Contract verified."));
        defects.save(new DefectEntity("Payment confirmation is unavailable", DefectSeverity.BLOCKER, release, run));

        DashboardResponse dashboard = dashboardService.get(release.getId());

        assertThat(dashboard.decision()).isEqualTo(ReleaseDecision.NO_GO);
        assertThat(dashboard.openBlockers()).isEqualTo(1);
        assertThat(dashboard.reasons()).contains("Open blocker defects: 1");
    }

    private ReleaseEntity release() {
        return releases.save(new ReleaseEntity("Test release " + UUID.randomUUID(), LocalDate.now().plusDays(7), ReleaseStatus.IN_TESTING));
    }

    private RequirementEntity requirement(ReleaseEntity release) {
        return requirements.save(new RequirementEntity("Checkout acceptance", "A customer can complete checkout.", RequirementPriority.HIGH, release));
    }
}
