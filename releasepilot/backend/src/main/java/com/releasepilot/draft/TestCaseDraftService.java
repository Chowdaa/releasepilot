package com.releasepilot.draft;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.releasepilot.requirement.RequirementEntity;
import com.releasepilot.requirement.RequirementPriority;
import com.releasepilot.requirement.RequirementRepository;
import com.releasepilot.testcase.TestCaseType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TestCaseDraftService {
    private final RequirementRepository requirementRepository;
    private final ObjectMapper objectMapper;
    private final String openAiApiKey;
    private final String openAiModel;
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    public TestCaseDraftService(
            RequirementRepository requirementRepository,
            ObjectMapper objectMapper,
            @Value("${openai.api-key:}") String openAiApiKey,
            @Value("${openai.model:gpt-4.1-mini}") String openAiModel) {
        this.requirementRepository = requirementRepository;
        this.objectMapper = objectMapper;
        this.openAiApiKey = openAiApiKey;
        this.openAiModel = openAiModel;
    }

    public TestCaseDraftBatchResponse generate(Long releaseId, CreateTestCaseDraftRequest request) {
        RequirementEntity requirement = requirementRepository.findById(request.requirementId())
                .filter(candidate -> candidate.getRelease().getId().equals(releaseId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requirement not found in this release."));
        if (!openAiApiKey.isBlank()) {
            try {
                List<TestCaseDraftResponse> drafts = generateWithOpenAi(requirement, request.acceptanceCriterion().trim());
                if (!drafts.isEmpty()) return response("OPENAI", drafts);
            } catch (Exception ignored) {
                // A runnable local fallback is intentionally used when no model is configured or reachable.
            }
        }
        return response("LOCAL_TEMPLATE", localDrafts(requirement, request.acceptanceCriterion().trim()));
    }

    private TestCaseDraftBatchResponse response(String source, List<TestCaseDraftResponse> drafts) {
        return new TestCaseDraftBatchResponse(source,
                "Drafts are suggestions only. Review and explicitly approve each test case before it is saved.", drafts);
    }

    private List<TestCaseDraftResponse> localDrafts(RequirementEntity requirement, String criterion) {
        String subject = requirement.getTitle();
        RequirementPriority priority = requirement.getPriority();
        return List.of(
                draft("Happy path — " + subject, "A test user is authenticated and required test data is available.",
                        "1. Start the workflow for: " + subject + ".\n2. Provide valid inputs.\n3. Complete the user action.",
                        "The acceptance criterion is met: " + criterion, priority),
                draft("Validation — " + subject, "A test user can access the workflow.",
                        "1. Start the workflow for: " + subject + ".\n2. Omit or invalidate one required input.\n3. Submit the action.",
                        "The user sees a clear validation message and no unintended action is completed.", priority),
                draft("Failure recovery — " + subject, "A test user is authenticated and a recoverable dependency failure can be simulated.",
                        "1. Start the workflow for: " + subject + ".\n2. Simulate a recoverable service or network failure.\n3. Retry or return to the workflow.",
                        "The failure is explained safely, no duplicate action occurs, and the user can recover.", priority)
        );
    }

    private TestCaseDraftResponse draft(String title, String preconditions, String steps, String expectedResult, RequirementPriority priority) {
        return new TestCaseDraftResponse(title, preconditions, steps, expectedResult, priority, TestCaseType.UI);
    }

    private List<TestCaseDraftResponse> generateWithOpenAi(RequirementEntity requirement, String criterion) throws Exception {
        String prompt = "Create exactly three concise software test-case drafts. Return JSON only with this shape: "
                + "{\\\"drafts\\\":[{\\\"title\\\":string,\\\"preconditions\\\":string,\\\"steps\\\":string,\\\"expectedResult\\\":string,\\\"priority\\\":\\\"CRITICAL|HIGH|MEDIUM|LOW\\\",\\\"type\\\":\\\"UI|API\\\"}]}. "
                + "Include happy path, validation boundary, and failure recovery. Do not include sensitive data. Requirement: "
                + requirement.getTitle() + ". Acceptance criterion: " + criterion;
        String payload = objectMapper.writeValueAsString(Map.of("model", openAiModel, "input", prompt));
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/responses"))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 300) throw new IllegalStateException("Model request failed.");
        JsonNode root = objectMapper.readTree(response.body());
        String output = root.path("output_text").asText("");
        if (output.isBlank()) {
            for (JsonNode item : root.path("output")) for (JsonNode content : item.path("content")) {
                if (content.has("text")) output = content.path("text").asText("");
            }
        }
        JsonNode draftNodes = objectMapper.readTree(output).path("drafts");
        if (!draftNodes.isArray()) return List.of();
        return java.util.stream.StreamSupport.stream(draftNodes.spliterator(), false).limit(3)
                .map(node -> new TestCaseDraftResponse(
                        node.path("title").asText(), node.path("preconditions").asText(), node.path("steps").asText(),
                        node.path("expectedResult").asText(), RequirementPriority.valueOf(node.path("priority").asText("MEDIUM")),
                        TestCaseType.valueOf(node.path("type").asText("UI"))))
                .filter(draft -> !draft.title().isBlank() && !draft.steps().isBlank())
                .toList();
    }
}
