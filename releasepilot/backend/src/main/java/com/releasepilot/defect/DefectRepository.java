package com.releasepilot.defect;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DefectRepository extends JpaRepository<DefectEntity, Long> {
    List<DefectEntity> findByReleaseIdOrderByIdAsc(Long releaseId);
    long countByReleaseIdAndStatusIn(Long releaseId, Collection<DefectStatus> statuses);
    long countByReleaseIdAndSeverityAndStatusIn(Long releaseId, DefectSeverity severity, Collection<DefectStatus> statuses);
}
