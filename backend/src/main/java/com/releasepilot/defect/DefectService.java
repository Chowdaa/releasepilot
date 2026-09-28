package com.releasepilot.defect;

import com.releasepilot.release.ReleaseEntity;
import com.releasepilot.release.ReleaseRepository;
import com.releasepilot.testrun.TestRunEntity;
import com.releasepilot.testrun.TestRunRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service @Transactional(readOnly = true)
public class DefectService {
    private final DefectRepository repository; private final ReleaseRepository releaseRepository; private final TestRunRepository testRunRepository;
    public DefectService(DefectRepository repository, ReleaseRepository releaseRepository, TestRunRepository testRunRepository) { this.repository = repository; this.releaseRepository = releaseRepository; this.testRunRepository = testRunRepository; }
    public List<DefectResponse> list(Long releaseId) { return repository.findByReleaseIdOrderByIdAsc(releaseId).stream().map(DefectResponse::from).toList(); }
    @Transactional public DefectResponse create(CreateDefectRequest request) {
        ReleaseEntity release = releaseRepository.findById(request.releaseId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Release not found."));
        TestRunEntity run = request.testRunId() == null ? null : testRunRepository.findById(request.testRunId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test run not found."));
        return DefectResponse.from(repository.save(new DefectEntity(request.summary().trim(), request.severity(), release, run)));
    }
}
