package com.releasepilot.testrun;

import com.releasepilot.testcase.TestCaseEntity;
import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "test_runs")
public class TestRunEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "test_case_id") private TestCaseEntity testCase;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TestResult result;
    @Column(nullable = false, length = 40) private String executionType;
    @Column(nullable = false) private Instant executedAt;
    @Column(length = 2000) private String notes;
    protected TestRunEntity() {}
    public TestRunEntity(TestCaseEntity testCase, TestResult result, String executionType, String notes) { this.testCase = testCase; this.result = result; this.executionType = executionType; this.notes = notes; this.executedAt = Instant.now(); }
    public Long getId() { return id; } public TestCaseEntity getTestCase() { return testCase; } public TestResult getResult() { return result; }
    public String getExecutionType() { return executionType; } public Instant getExecutedAt() { return executedAt; } public String getNotes() { return notes; }
}
