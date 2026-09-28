package com.releasepilot.defect;

import com.releasepilot.release.ReleaseEntity;
import com.releasepilot.testrun.TestRunEntity;
import jakarta.persistence.*;

@Entity @Table(name = "defects")
public class DefectEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 240) private String summary;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private DefectSeverity severity;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private DefectStatus status;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "release_id") private ReleaseEntity release;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "test_run_id") private TestRunEntity testRun;
    protected DefectEntity() {}
    public DefectEntity(String summary, DefectSeverity severity, ReleaseEntity release, TestRunEntity testRun) { this.summary = summary; this.severity = severity; this.release = release; this.testRun = testRun; this.status = DefectStatus.OPEN; }
    public Long getId() { return id; } public String getSummary() { return summary; } public DefectSeverity getSeverity() { return severity; } public DefectStatus getStatus() { return status; } public ReleaseEntity getRelease() { return release; } public TestRunEntity getTestRun() { return testRun; }
}
