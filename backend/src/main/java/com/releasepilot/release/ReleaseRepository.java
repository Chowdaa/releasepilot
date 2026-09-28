package com.releasepilot.release;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReleaseRepository extends JpaRepository<ReleaseEntity, Long> {
    boolean existsByName(String name);
}
