package com.releasepilot.requirement;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequirementRepository extends JpaRepository<RequirementEntity, Long> {
    List<RequirementEntity> findByReleaseIdOrderByIdAsc(Long releaseId);
}
