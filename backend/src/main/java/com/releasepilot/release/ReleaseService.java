package com.releasepilot.release;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReleaseService {
    private final ReleaseRepository releaseRepository;

    public ReleaseService(ReleaseRepository releaseRepository) {
        this.releaseRepository = releaseRepository;
    }

    public List<ReleaseResponse> list() {
        return releaseRepository.findAll().stream().map(ReleaseResponse::from).toList();
    }

    @Transactional
    public ReleaseResponse create(CreateReleaseRequest request) {
        if (releaseRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("A release with this name already exists.");
        }
        ReleaseEntity saved = releaseRepository.save(
                new ReleaseEntity(request.name().trim(), request.targetDate(), ReleaseStatus.PLANNED));
        return ReleaseResponse.from(saved);
    }
}
