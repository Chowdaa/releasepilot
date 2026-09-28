package com.releasepilot.requirement;

import com.releasepilot.release.ReleaseEntity;
import com.releasepilot.release.ReleaseRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class RequirementService {
    private final RequirementRepository requirementRepository;
    private final ReleaseRepository releaseRepository;

    public RequirementService(RequirementRepository requirementRepository, ReleaseRepository releaseRepository) {
        this.requirementRepository = requirementRepository;
        this.releaseRepository = releaseRepository;
    }

    public List<RequirementResponse> listForRelease(Long releaseId) {
        ensureReleaseExists(releaseId);
        return requirementRepository.findByReleaseIdOrderByIdAsc(releaseId)
                .stream()
                .map(RequirementResponse::from)
                .toList();
    }

    @Transactional
    public RequirementResponse create(Long releaseId, CreateRequirementRequest request) {
        ReleaseEntity release = releaseRepository.findById(releaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Release not found."));
        RequirementEntity saved = requirementRepository.save(new RequirementEntity(
                request.title().trim(), request.description().trim(), request.priority(), release));
        return RequirementResponse.from(saved);
    }

    private void ensureReleaseExists(Long releaseId) {
        if (!releaseRepository.existsById(releaseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Release not found.");
        }
    }
}
