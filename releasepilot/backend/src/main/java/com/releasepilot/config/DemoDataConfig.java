package com.releasepilot.config;

import com.releasepilot.defect.*;
import com.releasepilot.release.*;
import com.releasepilot.requirement.*;
import com.releasepilot.testcase.*;
import com.releasepilot.testrun.*;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataConfig {
    @Bean CommandLineRunner seedDemo(ReleaseRepository releases, RequirementRepository requirements, TestCaseRepository testCases, TestRunRepository testRuns, DefectRepository defects) {
        return args -> {
            if (releases.count() > 0) return;
            ReleaseEntity release = releases.save(new ReleaseEntity("ShopSphere 1.0 - Checkout", LocalDate.now().plusDays(14), ReleaseStatus.IN_TESTING));
            RequirementEntity payment = requirements.save(new RequirementEntity("Pay with a valid card", "A signed-in customer can successfully place an order using valid card details.", RequirementPriority.CRITICAL, release));
            RequirementEntity confirmation = requirements.save(new RequirementEntity("Show order confirmation", "After successful payment, the customer sees an order number and confirmation message.", RequirementPriority.HIGH, release));
            TestCaseEntity paymentApi = testCases.save(new TestCaseEntity("Create card payment via API", "Customer and cart exist", "POST a valid payment request", "API returns 201 and payment status APPROVED", RequirementPriority.CRITICAL, TestCaseType.API, payment));
            TestCaseEntity paymentUi = testCases.save(new TestCaseEntity("Complete checkout with card", "Customer has an item in cart", "Enter valid card details and submit checkout", "Order is placed successfully", RequirementPriority.CRITICAL, TestCaseType.UI, payment));
            TestCaseEntity confirmationUi = testCases.save(new TestCaseEntity("Display order confirmation", "A payment succeeds", "Finish checkout", "Order confirmation shows a unique order number", RequirementPriority.HIGH, TestCaseType.UI, confirmation));
            testRuns.save(new TestRunEntity(paymentApi, TestResult.PASS, "API", "Response contract validated."));
            TestRunEntity failed = testRuns.save(new TestRunEntity(paymentUi, TestResult.FAIL, "UI", "Checkout button remains disabled after valid card entry."));
            testRuns.save(new TestRunEntity(confirmationUi, TestResult.PASS, "UI", "Order number displayed."));
            defects.save(new DefectEntity("Checkout blocks valid Visa card payment", DefectSeverity.BLOCKER, release, failed));
        };
    }
}
