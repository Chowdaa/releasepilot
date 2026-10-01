package com.releasepilot.draft;

import java.util.List;

public record TestCaseDraftBatchResponse(String source, String reviewNotice, List<TestCaseDraftResponse> drafts) {}
