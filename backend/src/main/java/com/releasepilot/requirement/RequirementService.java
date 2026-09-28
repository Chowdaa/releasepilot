package com.releasepilot.requirement;

import com.releasepilot.release.ReleaseEntity;
import com.releasepilot.release.ReleaseRepository;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

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

    @Transactional
    public ImportRequirementsResponse importJiraCsv(Long releaseId, MultipartFile file) {
        ReleaseEntity release = releaseRepository.findById(releaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Release not found."));
        if (file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a CSV file to import.");
        List<String> skipped = new ArrayList<>(); int imported = 0;
        try (CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).build()
                .parse(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            for (CSVRecord row : parser) {
                String summary = value(row, "Summary");
                if (summary.isBlank()) { skipped.add("Row " + row.getRecordNumber() + ": missing Summary"); continue; }
                String issueType = value(row, "Issue Type");
                if (!issueType.isBlank() && !(issueType.equalsIgnoreCase("Story") || issueType.equalsIgnoreCase("Task") || issueType.equalsIgnoreCase("Requirement"))) {
                    skipped.add("Row " + row.getRecordNumber() + ": unsupported Issue Type '" + issueType + "'"); continue;
                }
                requirementRepository.save(new RequirementEntity(summary, value(row, "Description"), priority(value(row, "Priority")), release));
                imported++;
            }
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read Jira CSV: " + exception.getMessage());
        }
        return new ImportRequirementsResponse(imported, skipped);
    }

    private String value(CSVRecord row, String header) { return row.isMapped(header) ? row.get(header).trim() : ""; }
    private RequirementPriority priority(String priority) {
        return switch (priority.toLowerCase()) {
            case "highest", "critical" -> RequirementPriority.CRITICAL;
            case "high" -> RequirementPriority.HIGH;
            case "low", "lowest" -> RequirementPriority.LOW;
            default -> RequirementPriority.MEDIUM;
        };
    }

    private void ensureReleaseExists(Long releaseId) {
        if (!releaseRepository.existsById(releaseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Release not found.");
        }
    }
}
