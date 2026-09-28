package com.releasepilot.testcase;

import com.releasepilot.requirement.RequirementEntity;
import com.releasepilot.requirement.RequirementPriority;
import jakarta.persistence.*;

@Entity
@Table(name = "test_cases")
public class TestCaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 160) private String title;
    @Column(nullable = false, length = 1000) private String preconditions;
    @Column(nullable = false, length = 4000) private String steps;
    @Column(nullable = false, length = 2000) private String expectedResult;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private RequirementPriority priority;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TestCaseType type;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "requirement_id") private RequirementEntity requirement;
    protected TestCaseEntity() {}
    public TestCaseEntity(String title, String preconditions, String steps, String expectedResult, RequirementPriority priority, TestCaseType type, RequirementEntity requirement) {
        this.title = title; this.preconditions = preconditions; this.steps = steps; this.expectedResult = expectedResult;
        this.priority = priority; this.type = type; this.requirement = requirement;
    }
    public Long getId() { return id; } public String getTitle() { return title; } public String getPreconditions() { return preconditions; }
    public String getSteps() { return steps; } public String getExpectedResult() { return expectedResult; }
    public RequirementPriority getPriority() { return priority; } public TestCaseType getType() { return type; }
    public RequirementEntity getRequirement() { return requirement; }
}
